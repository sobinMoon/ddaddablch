package com.donation.ddb.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudentUser {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long sId;

    @Column(nullable = false,length=20)
    private String sName;

    @Column(nullable = false,length=100,unique=true)
    private String sNickname;

    @Column(nullable = false,length=100)
    private String sPassword;

    @Column(unique=true)
    private String sWalletAddress;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WalletAuthStatus sWalletAuthStatus=WalletAuthStatus.NONE;

    @Column(nullable = false,unique=true)
    private String sEmail;

    @Column(nullable = false)
    @Builder.Default
    private Boolean sIsActive=true; //기본값 설정

    @Column(length=255)
    private String sProfileImage;

}
