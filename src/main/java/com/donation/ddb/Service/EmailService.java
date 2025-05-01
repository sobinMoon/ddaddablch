package com.donation.ddb.Service;

import com.donation.ddb.Domain.VerificationToken;
import com.donation.ddb.Repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Autowired
    private final JavaMailSender javaMailSender;
    private final VerificationTokenRepository tokenRepository;

    @Value("ddaggeundb@gmail.com")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail){
        //이메일 도메인 검증 알고리즘 넣기 ***** 일단 구현하고

        //토큰 생성(기존에 있으면 삭제하기)
        Optional<VerificationToken> ov=tokenRepository.findByEmail(toEmail);
        if(ov.isPresent())
        {
            tokenRepository.delete(ov.get());
        }

        String token= UUID.randomUUID().toString();
        VerificationToken verificationToken=new VerificationToken();
        verificationToken.setEmail(toEmail);
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(12)); //12시간 유효
        //토큰 저장
        tokenRepository.save(verificationToken);

        //이메일 생성 & 발송
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("회원가입 인증 메일입니다");
        message.setText("안녕하세요,\n\n회원가입을 완료하려면 다음 숫자를 입력하세요:\n"
                + token
                + "\n\n이 번호는 12시간 동안 유효합니다.");

        javaMailSender.send(message);
    }

    public boolean verifyEmail(String token) {
        Optional<VerificationToken> ot = tokenRepository.findByToken(token);
        if (ot.isPresent()) {
            VerificationToken verificationToken = ot.get();

            //만료되었으면 false반환
            if (verificationToken.isExpired()) return false;

            verificationToken.setVerified(true);
            tokenRepository.save(verificationToken);
            return true;
        }
        return false;
    }
    //isEmailVerified필요하면 구현나중에
}
