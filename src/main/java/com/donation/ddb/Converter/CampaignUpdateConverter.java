package com.donation.ddb.Converter;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.CampaignUpdate;
import com.donation.ddb.Dto.Request.CampaignUpdateRequestDto;
import com.donation.ddb.Dto.Response.CampaignUpdateResponseDto;

public class CampaignUpdateConverter {
    public static CampaignUpdate toCampaignUpdate(CampaignUpdateRequestDto.JoinDto request, String imagePath, Campaign campaign) {
        return CampaignUpdate.builder()
                .cuTitle(request.getTitle())
                .cuContent(request.getContent())
                .cuImageUrl(imagePath)
                .campaign(campaign)
                .build();
    }

    public static CampaignUpdateResponseDto.JoinResultDto toJoinResultDto(CampaignUpdate campaignUpdate) {
        return CampaignUpdateResponseDto.JoinResultDto.builder()
                .id(campaignUpdate.getCuId())
                .createdAt(campaignUpdate.getCreatedAt())
                .build();
    }

    public static CampaignUpdateResponseDto.CampaignUpdateDto toCampaignUpdateDto(CampaignUpdate campaignUpdate) {
        if (campaignUpdate == null) {
            return null; // Handle null case if needed
        }

        return CampaignUpdateResponseDto.CampaignUpdateDto.builder()
                .id(campaignUpdate.getCuId())
                .title(campaignUpdate.getCuTitle())
                .content(campaignUpdate.getCuContent())
                .imageUrl(campaignUpdate.getCuImageUrl())
                .createdAt(campaignUpdate.getCreatedAt())
                .updatedAt(campaignUpdate.getUpdatedAt())
                .build();
    }
}