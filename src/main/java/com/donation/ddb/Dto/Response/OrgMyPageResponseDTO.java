package com.donation.ddb.Dto.Response;


import com.donation.ddb.Domain.Enums.CampaignStatusFlag;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgMyPageResponseDTO {

    // 기본 정보
    private Long oid;
    private String onName;
    private String oEmail;
    private String oDescription;
    private String oProfileImage;
    private String oBusinessNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // 총 모금액 (모든 캠페인 합계)
    private BigDecimal totalRaisedAmount;

    // 캠페인 상태별 분류
    private List<CampaignSummaryDTO> completedCampaigns;    // COMPLETED
    private List<CampaignSummaryDTO> activeCampaigns;       // FUNDED + IN_PROGRESS
    private List<CampaignSummaryDTO> fundraisingCampaigns;  // FUNDRAISING

    @Getter
    @Builder
    @NoArgsConstructor
    //@AllArgsConstructor
    public static class CampaignSummaryDTO {
        private Long campaignId;
        private String campaignName;
        private String organizationName;
        private String description;
        private BigDecimal currentAmount;
        private Integer goalAmount;
        private String imageUrl;
        private CampaignStatusFlag status;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        // QueryDSL용 생성자
        public CampaignSummaryDTO(Long campaignId, String campaignName, String organizationName,
                                  String description, BigDecimal currentAmount, Integer goalAmount,
                                  String imageUrl, CampaignStatusFlag status, LocalDateTime createdAt) {
            this.campaignId = campaignId;
            this.campaignName = campaignName;
            this.organizationName = organizationName;
            this.description = description;
            this.currentAmount = currentAmount;
            this.goalAmount = goalAmount;
            this.imageUrl = imageUrl;
            this.status = status;
            this.createdAt = createdAt;
        }
    }
}