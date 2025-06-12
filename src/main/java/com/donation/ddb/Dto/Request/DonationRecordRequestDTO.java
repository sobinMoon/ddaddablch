package com.donation.ddb.Dto.Request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationRecordRequestDTO {
    @NotBlank(message = "트랜잭션 해시는 필수입니다")
    private String transactionHash;

    @NotBlank(message="기부자 지갑 주소는 필수입니다.")
    private String donorWalletAddress;

    @NotBlank(message = "수혜자 지갑 주소는 필수입니다")
    private String campaignWalletAddress;

    @NotNull(message = "기부 금액은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = false, message = "기부 금액은 0보다 커야 합니다")
    //@Positive(message = "기부 금액은 0보다 커야 합니다")
    private BigDecimal amount;

    // 캠페인 ID
    @NotNull(message = "캠페인 ID는 필수입니다.")
    private Long campaignId;

    // 수혜자 ID
    @NotNull(message = "수혜자 ID는 필수입니다")
    private Long userId;

    // 기부 메시지 -> 선택임.
    private String message;

    // 익명 기부 여부 -> 나중에 추가하기
    //private Boolean isAnonymous = false;

    // 블록 번호 (프론트에서 받을 수 있다면..??)
    //private Long blockNumber;
}
