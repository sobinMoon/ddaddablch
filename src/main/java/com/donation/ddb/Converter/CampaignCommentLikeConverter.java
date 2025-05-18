package com.donation.ddb.Converter;

import com.donation.ddb.Domain.CampaignCommentLike;
import com.donation.ddb.Dto.Response.CampaignCommentLikeDto;

public class CampaignCommentLikeConverter {

    public static CampaignCommentLikeDto.toggleResultDto toDto(CampaignCommentLike campaignCommentLike) {
        return CampaignCommentLikeDto.toggleResultDto.builder()
                .ccId(campaignCommentLike.getCampaignComment().getCcId())
                .build();
    }
}
