package com.donation.ddb.Service;

import com.donation.ddb.Domain.*;
import com.donation.ddb.Dto.Request.WalletAddressVerifyRequestDto;
import com.donation.ddb.Repository.AuthEventRepository;
import com.donation.ddb.Repository.RefreshTokenRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private final StudentUserRepository studentUserRepository;
    private final AuthEventRepository authEventRepository;
    private final RefreshTokenRepository refreshTokenRepository;


    @Transactional
    public String generateMessage(String email,String walletAddress){
        //사용자 찾기
        StudentUser su=studentUserRepository.findBysEmail(email)
                .orElseThrow(()-> new DataNotFoundException("없는 email입니다."));

        //nonce 생성하기 , UUID: 36자 고유 문자열이어서 충돌 가능성 거의 없음.
        String nonce= UUID.randomUUID().toString();

        //AuthEvent 생성하기
        AuthEvent authEvent=new AuthEvent();
        authEvent.setUser(su);
        authEvent.setWalletAddress(walletAddress);
        authEvent.setNonce(nonce);
        authEvent.setCreatedAt(LocalDateTime.now());
        authEvent.setEventType(WalletAuthStatus.PENDING); //인증 대기 상태로 설정

        //인증 메시지 생성
        String message=authEvent.generateAuthMessage();

        //사용자 지갑 주소 업데이트
        if(su.getSWalletAddress()==null || !su.getSWalletAddress().equals(walletAddress)){
            su.setSWalletAddress(walletAddress);
        }

        authEventRepository.save(authEvent);

        return message;
    }

    @Transactional
    public Boolean verifySignature(WalletAddressVerifyRequestDto requestDto){//주소 지갑의 진짜 주인인지 확인
        try{

            String walletAddress=requestDto.getWalletAddress();
            String signature = requestDto.getSignature();//metamask가 생성한 서명값(0x+130자리 hex 문자열)
            String message=requestDto.getMessage();

            //지갑 주소로 사용자 조회하기
            StudentUser user=studentUserRepository.findBysWalletAddress(walletAddress)
                    .orElseThrow(()-> new DataNotFoundException("해당 지갑 주소를 가진 사용자를 찾을 수 없습니다"));

            //사용자의 최신 인증 이벤트 조회하기
            AuthEvent authEvent=authEventRepository
                    .findByUser(user)
                    .orElseThrow(()->new DataNotFoundException("해당 지갑 주소의 인증 이벤트 없습니다"));

            //서명 정보 authevent에 저장하기
            authEvent.setSignature(signature);

            //이더리움에서 사용하는 표준 메시지 서명 포맷 적용

            String requiredMessage=authEvent.getMessage();

            if(!requiredMessage.equals(message)) {
                throw new DataNotFoundException("발급받은 message와 일치하지 않습니다.");
            }

            //메시지 전체에 대한 Keccak-256 해시 적용 (Ethereum 서명 알고리즘에서 쓰이는거)
            String prefix = "\u0019Ethereum Signed Message:\n" + message.length();
            String prefixedMessage = prefix + message;

            // 메시지 정확히 비교하기 (공백 포함)
            System.out.println("DB 메시지: '" + requiredMessage + "'");
            System.out.println("요청 메시지: '" + message + "'");

            //byte[] msgHash=Hash.sha3(prefixedMessage.getBytes(StandardCharsets.UTF_8));
            byte[] msgHash = Sign.getEthereumMessageHash(message.getBytes(StandardCharsets.UTF_8));
            //signature 문자열을 바이트 배열로 변환
            //서명이 "0x"로 시작하면 제거해야함.
            if(signature.startsWith("0x")){
                signature=signature.substring(2);
            }
            byte[] signatureBytes=Numeric.hexStringToByteArray(signature);

            // 서명 길이는 65바이트여야 함 (r:32 + s:32 + v:1)

            if (signatureBytes.length != 65){
                throw new IllegalArgumentException("Signature length is invalid: expected 65 bytes, but got " + signatureBytes.length);}

            // v값은 마지막 바이트 (65번째 바이트)이며, v < 27이면 +27 보정이 필요함
            byte v = signatureBytes[64];
            if (v < 27) v += 27;

            // signatureBytes를 r, s, v 세 값으로 분리하여 SignatureData 객체 생성
            Sign.SignatureData sigData = new Sign.SignatureData(
                    v,
                    Arrays.copyOfRange(signatureBytes, 0, 32),   // r
                    Arrays.copyOfRange(signatureBytes, 32, 64)   // s
            );

            //해시 + 서명 데이터 ->  공개키 복원
            //BigInteger publicKey=Sign.signedMessageToKey(msgHash,sigData);
            BigInteger publicKey = Sign.signedMessageHashToKey(msgHash, sigData);


            //복원된 퍼블릭 키로부터 Ethereum지갑 주소 생성
            String recoveredAddress="0x"+Keys.getAddress(publicKey);
            System.out.println("원본 주소: " + walletAddress);
            System.out.println("복원된 주소: " + recoveredAddress);
            //대소문자 구분않고 문자열비교함. 이더리움 주소 16진수인데 대소문자 혼용될 수 있어서.
            Boolean isverified= walletAddress.equalsIgnoreCase(recoveredAddress);
            if(isverified){
                authEvent.setEventType(WalletAuthStatus.CONNECTED);
                return true;
            }else{
                //
                authEvent.setEventType(WalletAuthStatus.FAILED);
                return false;
            }

        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public void deleteToken(String token){
            RefreshToken refreshToken=refreshTokenRepository.findByToken(token)
                    .orElseThrow(()-> new DataNotFoundException("토큰이 존재하지 않습니다."));
            // DB에서 해당 리프레시 토큰 삭제
            refreshTokenRepository.deleteByToken(refreshToken.toString());

    }
}
