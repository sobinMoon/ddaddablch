package com.donation.ddb.Converter;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.OrganizationUser;
import com.donation.ddb.Dto.Request.CampaignRequestDto;
import com.donation.ddb.Dto.Response.CampaignResponse;
import com.donation.ddb.Repository.projection.CampaignWithUpdate;

import java.util.List;

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

    public static CampaignResponse.RecentUpdateDto toRecentUpdateDto(CampaignWithUpdate campaignWithUpdate) {
        return CampaignResponse.RecentUpdateDto.builder()
                .campaignId(campaignWithUpdate.getCampaignId())
                .name(campaignWithUpdate.getTitle())
                .previewContent(campaignWithUpdate.getPreviewContent().length() > 200
                        ? campaignWithUpdate.getPreviewContent().substring(0, 200) + "..." :
                        campaignWithUpdate.getPreviewContent())
                .createdAt(campaignWithUpdate.getCreatedAt())
                .build();
    }

    public static List<CampaignResponse.RecentUpdateDto> toRecentUpdateListDto(List<CampaignWithUpdate> campaignList) {
        return campaignList.stream()
                .map(CampaignConverter::toRecentUpdateDto)
                .toList();
    }
}