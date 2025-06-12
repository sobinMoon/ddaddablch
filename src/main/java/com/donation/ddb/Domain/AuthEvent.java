package com.donation.ddb.Domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class AuthEvent {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name="sId")
    private StudentUser user;

    @Enumerated(EnumType.STRING)
    private WalletAuthStatus eventType=WalletAuthStatus.NONE;

    @Column(nullable=false)
    private String walletAddress;

    @Column(nullable=false)
    private String nonce;

    //검증 후에 사용자 서명 결과 저장
    private String signature;

    //검증 성공 시간
    @CreatedDate
    private LocalDateTime createdAt;

    private String message;

    //편의 메서드 : 인증 메시지 생성
    @Transient // 데이터베이스에 저장되지 않음 -->>>>>??
    public String generateAuthMessage(){
        String message="Wallet verification for ddb\n"+
                "Nonce: "+this.nonce+"\n"
                +"Timestamp"+this.createdAt;
        //String prefix = "\u0019Ethereum Signed Message:\n" + message.length();
        this.setMessage(message);
        return message;
    }
}
