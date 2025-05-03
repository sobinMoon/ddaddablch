package com.donation.ddb.Domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String email;

    @NotBlank
    private String token;

    @CreatedDate
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime expiryDate; //유효 시간은 만들어진 시간 +12h

    //boolean은 notblank필요없
    private boolean verified=false;

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expiryDate);
    }


    //정적 생성 메소드
    public static VerificationToken createToken(
            String email, String token
    ){
        VerificationToken verificationToken=new VerificationToken();
        verificationToken.setEmail(email);
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(
                LocalDateTime.now().plusHours(12)); //12시간 유효
        verificationToken.setCreatedAt(
                LocalDateTime.now()
        );

        return verificationToken;
    }
}
