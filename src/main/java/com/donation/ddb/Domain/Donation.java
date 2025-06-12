package com.donation.ddb.Domain;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Donation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dId;

    //기부자 정보
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="s_id")
    private StudentUser studentUser;

    // 캠페인 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    // 기부 금액 (ETH)
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal amount;

    //블록체인 트랜잭션 해시
    @Column(nullable=false, unique=true)
    private String transactionHash;

    // 기부자 지갑 주소
    @Column(nullable = false)
    private String donorWalletAddress;

    // 수혜자 지갑 주소
    @Column(nullable = false)
    private String campaignWalletAddress;

    @Column
    private Long nftTokenId; //받은 NFT 토큰 ID

    // 블록 번호 -> 필요하면 넣기
    //private Long blockNumber;

    //트랜잭션 상태
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DonationStatus status=DonationStatus.SUCCESS;

    //기부 메시지
    @Column(length=500)
    private String message;

//    //익명 기부 여부
//    @Builder.Default
//    private Boolean isAnonymous=false;

}

