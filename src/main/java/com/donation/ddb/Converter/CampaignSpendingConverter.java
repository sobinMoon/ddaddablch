package com.donation.ddb.Converter;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.CampaignPlan;
import com.donation.ddb.Domain.CampaignSpending;
import com.donation.ddb.Dto.Request.CampaignPlanRequestDto;
import com.donation.ddb.Dto.Request.CampaignSpendingRequestDto;

public class CampaignSpendingConverter {
    public static CampaignSpending toCampaignSpendings(CampaignSpendingRequestDto.JoinDto request, Campaign campaign) {
        return CampaignSpending.builder()
                .campaign(campaign)
                .csTitle(request.getTitle())
                .csAmount(request.getAmount())
                .build();
    }
}