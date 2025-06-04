package com.donation.ddb.Converter;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.CampaignPlan;
import com.donation.ddb.Dto.Request.CampaignPlanRequestDto;

public class CampaignPlanConverter {
    public static CampaignPlan toCampaignPlan(CampaignPlanRequestDto.JoinDto request, Campaign campaign) {
        return CampaignPlan.builder()
                .campaign(campaign)
                .cpTitle(request.getTitle())
                .cpAmount(request.getAmount())
                .build();
    }
}