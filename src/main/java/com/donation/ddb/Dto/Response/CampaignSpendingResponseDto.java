package com.donation.ddb.Dto.Response;

import com.donation.ddb.Domain.CampaignSpending;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignSpendingResponseDto {
    private Long id;
    private String title;
    private int amount;

    public static CampaignSpendingResponseDto from(CampaignSpending campaignSpending) {
        return CampaignSpendingResponseDto.builder()
                .id(campaignSpending.getCsId())
                .title(campaignSpending.getCsTitle())
                .amount(campaignSpending.getCsAmount())
                .build();
    }
}
