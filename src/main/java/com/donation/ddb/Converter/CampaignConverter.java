package com.donation.ddb.Converter;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.OrganizationUser;
import com.donation.ddb.Dto.Request.CampaignRequestDto;
import com.donation.ddb.Dto.Response.CampaignResponse;

public class CampaignConverter {
    public static Campaign toCampaign(CampaignRequestDto.JoinDto request, OrganizationUser user) {
        return Campaign.builder()
                .cName(request.getName())
                .cImageUrl(request.getImageUrl())
                .cDescription(request.getDescription())
                .cGoal(request.getGoal())
                .cWalletAddress(request.getWalletAddress())
                .cCategory(request.getCategory())
                .donateStart(request.getDonateStart())
                .donateEnd(request.getDonateEnd())
                .businessStart(request.getBusinessStart())
                .businessEnd(request.getBusinessEnd())
                .organizationUser(user)
                .build();
    }

    public static CampaignResponse.JoinResultDto toJoinResult(Campaign campaign) {
        return CampaignResponse.JoinResultDto.builder()
                .id(campaign.getCId())
                .createdAt(campaign.getCreatedAt())
                .build();
    }
}
