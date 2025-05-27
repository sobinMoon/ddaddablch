package com.donation.ddb.Service.EmailService;

import com.donation.ddb.Domain.Exception.TokenExpiredException;
import com.donation.ddb.Domain.VerificationToken;
import com.donation.ddb.Repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.donation.ddb.Domain.VerificationToken.createToken;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final VerificationTokenRepository tokenRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Transactional
    public void sendVerificationEmail(String toEmail) {
        try{
        //토큰 생성(기존에 있으면 삭제하기)
        Optional<VerificationToken> ov = tokenRepository.findByEmail(toEmail);
        if (ov.isPresent()) tokenRepository.delete(ov.get());

        //토큰 저장
        String token = UUID.randomUUID().toString();
        tokenRepository.save(createToken(toEmail, token));

        //이메일 생성 & 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("회원가입 인증 메일입니다");
        message.setText("안녕하세요,\n\n회원가입을 완료하려면 다음 숫자를 입력하세요:\n"
                + token
                + "\n\n이 번호는 12시간 동안 유효합니다.");
        javaMailSender.send(message);
        //예외처리 추가해주기
    } catch(MailException e){
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.",e);
        }
    }

    @Transactional
    public boolean verifyEmail(String token) {
        Optional<VerificationToken> ot = tokenRepository.findByToken(token);
        if (ot.isPresent()) {
            VerificationToken verificationToken = ot.get();

            //토큰 만료되었으면 false 반환
            if (verificationToken.isExpired())
                throw new TokenExpiredException("인증 번호가 만료되었습니다.");
            //토큰 verified true로 바꾸기
            verificationToken.setVerified(true);
            tokenRepository.save(verificationToken);
            return true;
        }
        else return false;
    }
}
