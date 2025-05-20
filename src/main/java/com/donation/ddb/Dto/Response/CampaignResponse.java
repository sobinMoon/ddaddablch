package com.donation.ddb.Dto.Response;

import com.donation.ddb.Domain.CampaignCategory;
import com.donation.ddb.Domain.CampaignPlan;
import com.donation.ddb.Domain.CampaignStatusFlag;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

//@Getter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
public class CampaignResponse {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CampaignListDto {
        private Long id;
        private String name;
        private String imageUrl;
        private String description;
        private Integer goal;
        private Integer currentAmount;
        private CampaignCategory category;
        private Long donateCount;
        private LocalDate donateStart;
        private LocalDate donateEnd;
        private LocalDate businessStart;
        private LocalDate businessEnd;
        private CampaignStatusFlag statusFlag;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CampaignDetailDto {
        private Long id;
        private String name;
        private String imageUrl;
        private String description;
        private Integer goal;
        private Integer currentAmount;
        private CampaignCategory category;
        private Long donateCount;
        private LocalDate donateStart;
        private LocalDate donateEnd;
        private LocalDate businessStart;
        private LocalDate businessEnd;
        private CampaignStatusFlag statusFlag;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private OrganizationResponse.OrganizationDetailDto organization;
        private List<CampaignPlanResponseDto> campaignPlans;
        private List<CampaignSpendingResponseDto> campaignSpendings;
    }
}
