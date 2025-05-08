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
public class OrganizationUser {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long oId;

    @Column(nullable = false,length=20)
    private String oName;

    @Column(nullable = false,unique=true)
    private String sEmail;

    @Column(nullable = false,length=100)
    private String oPassword;

    @Column(unique=true)
    private String oWalletAddress;

    @Column(length=100)
    private String oDescription;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WalletAuthStatus oWalletAuthStatus=WalletAuthStatus.NONE;

    @Column(nullable = false)
    @Builder.Default
    private Boolean oIsActive=true; //기본값 설정

    @Column(length=255)
    private String sProfileImage;

}
