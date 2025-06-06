package com.donation.ddb.Service.WalletService;

import com.donation.ddb.Domain.*;
import com.donation.ddb.Domain.Exception.DataNotFoundException;
import com.donation.ddb.Dto.Request.WalletAddressVerifyRequestDto;
import com.donation.ddb.Repository.AuthEventRepository;
import com.donation.ddb.Repository.RefreshTokenRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final StudentUserRepository studentUserRepository;
    private final AuthEventRepository authEventRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 지갑 인증 메시지 생성 및 AuthEvent ID 반환
     */
    @Transactional
    public Long generateMessageByUserId(Long userId, String walletAddress) {
        log.info("메시지 생성 시작 - 유저id: {}, walletAddress: {}", userId, walletAddress);

        // 사용자 찾기
        StudentUser su = studentUserRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("없는 사용자 id입니다."));

        // 해당 지갑 주소의 기존 AuthEvent가 있다면 삭제 (선택사항)
        authEventRepository.findByWalletAddress(walletAddress)
                .ifPresent(event -> {
                    log.info("기존 AuthEvent 삭제: {}", event.getId());
                    authEventRepository.delete(event);
                    authEventRepository.flush();
                });

        // nonce 생성
        String nonce = UUID.randomUUID().toString();

        // AuthEvent 생성
        AuthEvent authEvent = new AuthEvent();
        authEvent.setUser(su);
        authEvent.setWalletAddress(walletAddress);
        authEvent.setNonce(nonce);
        authEvent.setCreatedAt(LocalDateTime.now());
        authEvent.setEventType(WalletAuthStatus.PENDING);

        // 인증 메시지 생성
        String message = authEvent.generateAuthMessage();
        log.info("생성된 메시지: {}", message);

        // 사용자 지갑 주소 업데이트
        if (! su.hasWallet(walletAddress) || su.getWalletAddresses()==null) {
            su.addWallet(walletAddress);
        }

        // AuthEvent 저장
        AuthEvent savedAuthEvent = authEventRepository.save(authEvent);
        log.info("AuthEvent 저장 완료 - ID: {}, nonce: {}", savedAuthEvent.getId(), nonce);

        return savedAuthEvent.getId();
    }

    @Transactional
    public Boolean verifySignature(WalletAddressVerifyRequestDto requestDto) {
        try {
            Long authEventId = requestDto.getAuthEventId();
            String walletAddress = requestDto.getWalletAddress();
            String signature = requestDto.getSignature();
            String message = requestDto.getMessage();

            log.info("서명 검증 시작 - authEventId: {}, walletAddress: {}", authEventId, walletAddress);
            log.info("받은 메시지: {}", message);
            log.info("받은 서명: {}", signature);

            // AuthEvent ID와 지갑 주소로 AuthEvent 찾기 (보안을 위해 둘 다 확인)
            AuthEvent authEvent = authEventRepository
                    .findByIdAndWalletAddress(authEventId, walletAddress)
                    .orElseThrow(() -> new DataNotFoundException("해당 ID와 지갑 주소에 맞는 인증 이벤트가 없습니다."));

            log.info("찾은 AuthEvent - ID: {}, nonce: {}, status: {}",
                    authEvent.getId(), authEvent.getNonce(), authEvent.getEventType());

            // PENDING 상태인지 확인
            if (authEvent.getEventType() != WalletAuthStatus.PENDING) {
                log.error("AuthEvent가 PENDING 상태가 아님: {}", authEvent.getEventType());
                throw new DataNotFoundException("인증 이벤트가 대기 상태가 아닙니다.");
            }

            // 서명 정보 저장
            authEvent.setSignature(signature);

            // 저장된 메시지와 요청 메시지 비교
            String storedMessage = authEvent.getMessage();
            log.info("저장된 메시지: {}", storedMessage);

            if (!storedMessage.equals(message)) {
                log.error("메시지 불일치 - 저장된: {}, 요청된: {}", storedMessage, message);
                authEvent.setEventType(WalletAuthStatus.FAILED);
                throw new DataNotFoundException("발급받은 message와 일치하지 않습니다.");
            }

            // 이더리움 서명 검증
            boolean isVerified = verifyEthereumSignature(message, signature, walletAddress);

            if (isVerified) {
                authEvent.setEventType(WalletAuthStatus.CONNECTED);
                // 사용자의 지갑 상태도 업데이트
                authEvent.getUser().setSWalletAuthStatus(WalletAuthStatus.CONNECTED);
                log.info("서명 검증 성공");
                return true;
            } else {
                authEvent.setEventType(WalletAuthStatus.FAILED);
                log.error("서명 검증 실패");
                return false;
            }

        } catch (Exception e) {
            log.error("서명 검증 중 오류 발생", e);
            return false;
        }
    }

    /**
     * 이더리움 서명 검증 로직
     */
    private boolean verifyEthereumSignature(String message, String signature, String expectedAddress) {
        try {
            // 서명에서 "0x" 제거
            if (signature.startsWith("0x")) {
                signature = signature.substring(2);
            }

            // 서명을 바이트 배열로 변환
            byte[] signatureBytes = Numeric.hexStringToByteArray(signature);

            // 서명 길이 검증 (65바이트)
            if (signatureBytes.length != 65) {
                log.error("잘못된 서명 길이: expected 65 bytes, got {}", signatureBytes.length);
                return false;
            }

            // 이더리움 메시지 해시 생성
            byte[] msgHash = Sign.getEthereumMessageHash(message.getBytes(StandardCharsets.UTF_8));

            // v 값 처리
            byte v = signatureBytes[64];
            if (v < 27) v += 27;

            // SignatureData 객체 생성
            Sign.SignatureData sigData = new Sign.SignatureData(
                    v,
                    Arrays.copyOfRange(signatureBytes, 0, 32),   // r
                    Arrays.copyOfRange(signatureBytes, 32, 64)   // s
            );

            // 공개키 복원
            BigInteger publicKey = Sign.signedMessageHashToKey(msgHash, sigData);

            // 주소 복원
            String recoveredAddress = "0x" + Keys.getAddress(publicKey);

            log.info("원본 주소: {}", expectedAddress);
            log.info("복원된 주소: {}", recoveredAddress);

            // 대소문자 구분 없이 비교
            return expectedAddress.equalsIgnoreCase(recoveredAddress);

        } catch (Exception e) {
            log.error("이더리움 서명 검증 중 오류", e);
            return false;
        }
    }

    /**
     * 특정 AuthEvent 조회 (디버깅용)
     */
    public AuthEvent getAuthEventById(Long authEventId) {
        return authEventRepository.findById(authEventId)
                .orElseThrow(() -> new DataNotFoundException("해당 ID의 AuthEvent를 찾을 수 없습니다."));
    }

    /**
     * 지갑 주소로 최신 AuthEvent 조회 (디버깅용)
     */
    public AuthEvent getAuthEventByWallet(String walletAddress) {
        return authEventRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new DataNotFoundException("해당 지갑 주소의 AuthEvent를 찾을 수 없습니다."));
    }

    /**
     * 리프레시 토큰 삭제
     */
    @Transactional
    public void deleteToken(String token) {
        try {
            RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                    .orElseThrow(() -> new DataNotFoundException("토큰이 존재하지 않습니다."));

            refreshTokenRepository.deleteByToken(token);
            log.info("RefreshToken 삭제 완료: {}", refreshToken);
        } catch (Exception e) {
            log.error("RefreshToken 삭제 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
}