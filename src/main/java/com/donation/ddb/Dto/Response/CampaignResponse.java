package com.donation.ddb.Dto.Response;

import com.donation.ddb.Domain.Enums.CampaignCategory;
import com.donation.ddb.Domain.Enums.CampaignStatusFlag;
import lombok.*;

import java.math.BigDecimal;
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
    public static class JoinResultDto {
        private Long id;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CampaignListDto {
        private Long id;
        private String name;
        private String imageUrl;
        private String description;
        private Integer goal;
        private BigDecimal currentAmount;
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
        private BigDecimal currentAmount;
        private CampaignCategory category;
        private Long donateCount;
        private LocalDate donateStart;
        private LocalDate donateEnd;
        private LocalDate businessStart;
        private LocalDate businessEnd;
        private CampaignStatusFlag statusFlag;
        private String walletAddress;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private OrganizationResponse.OrganizationDetailDto organization;
        private CampaignUpdateResponseDto.CampaignUpdateDto campaignUpdate;
        private List<CampaignPlanResponseDto> campaignPlans;
        private List<CampaignSpendingResponseDto> campaignSpendings;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentUpdateDto {
        private Long campaignId;
        private String name;
        private String previewContent;
        private LocalDateTime createdAt;
    }
}
