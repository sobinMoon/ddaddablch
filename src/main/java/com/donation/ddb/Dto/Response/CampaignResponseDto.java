package com.donation.ddb.Dto.Response;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.CampaignCategory;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignResponseDto {
    private Long cId;

    private String cName;

    private String cImageUrl;

    private String cDescription;

    private int cGoal;

    private int cCurrentAmount = 0;

    private CampaignCategory cCategory = CampaignCategory.NAME1;

    private long donateCount = 0;

    private LocalDate donateStart;

    private LocalDate donateEnd;

    private LocalDate businessStart;

    private LocalDate businessEnd;

    private String cStatusFlag;

    private String createdAt;

    private String updatedAt;

    public static CampaignResponseDto toDto(Campaign campaign) {
        return CampaignResponseDto.builder()
                .cId(campaign.getCId())
                .cName(campaign.getCName())
                .cImageUrl(campaign.getCImageUrl())
                .cDescription(campaign.getCDescription())
                .cGoal(campaign.getCGoal())
                .cCurrentAmount(campaign.getCCurrentAmount())
                .cCategory(campaign.getCCategory())
                .donateCount(campaign.getDonateCount())
                .donateStart(campaign.getDonateStart())
                .donateEnd(campaign.getDonateEnd())
                .businessStart(campaign.getBusinessStart())
                .businessEnd(campaign.getBusinessEnd())
                .cStatusFlag(campaign.getCStatusFlag().name())
                .createdAt(campaign.getCreatedAt().toString())
                .updatedAt(campaign.getUpdatedAt().toString())
                .build();
    }

//    public static CampaignResponseDto toDto(Campaign campaign) {
//        CampaignResponseDto campaignResponseDto = new CampaignResponseDto();
//        campaignResponseDto.setCId(campaign.getCId());
//        campaignResponseDto.setCName(campaign.getCName());
//        campaignResponseDto.setCImageUrl(campaign.getCImageUrl());
//        campaignResponseDto.setCDescription(campaign.getCDescription());
//        campaignResponseDto.setCGoal(campaign.getCGoal());
//        campaignResponseDto.setCCurrentAmount(campaign.getCCurrentAmount());
//        campaignResponseDto.setCCategory(campaign.getCCategory());
//        campaignResponseDto.setDonateCount(campaign.getDonateCount());
//        campaignResponseDto.setDonateStart(campaign.getDonateStart());
//        campaignResponseDto.setDonateEnd(campaign.getDonateEnd());
//        campaignResponseDto.setBusinessStart(campaign.getBusinessStart());
//        campaignResponseDto.setBusinessEnd(campaign.getBusinessEnd());
//        campaignResponseDto.setCStatusFlag(campaign.getCStatusFlag().name());
//        campaignResponseDto.setCreatedAt(campaign.getCreatedAt().toString());
//        campaignResponseDto.setUpdatedAt(campaign.getUpdatedAt().toString());
//
//        return campaignResponseDto;
//    }
}
