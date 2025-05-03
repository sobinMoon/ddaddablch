package com.donation.ddb.Domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class StudentUser {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long sId;

    @Column(nullable = false,length=20)
    private String sName;

    @Column(nullable = false,length=100,unique=true)
    private String sNickname;


    //비밀번호는 저장전에 BCrypt 등의 해시 알고리즘 사용하기
    @Column(nullable = false,length=100)
    private String sPassword;

    @Column(unique=true)
    private String sWalletAddress;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WalletAuthStatus sWalletAuthStatus=WalletAuthStatus.NONE;

    @Column(nullable = false,unique=true)
    @Email(message="올바른 이메일 형식이어야 합니다.")
    private String sEmail;

    @Column(nullable = false)
    @Builder.Default
    private Boolean sIsActive=true; //기본값 설정

    @Column(length=255)
    private String sProfileImage;

    @CreatedDate
    @Column(nullable=false,updatable=false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable=false)
    private LocalDateTime updatedAt;

}
