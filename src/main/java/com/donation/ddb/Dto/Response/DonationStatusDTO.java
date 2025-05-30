package com.donation.ddb.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationStatusDTO {
    private BigDecimal totalAmount; //총 기부 금액
    private Integer totalCount; //총 기부 횟수
}
