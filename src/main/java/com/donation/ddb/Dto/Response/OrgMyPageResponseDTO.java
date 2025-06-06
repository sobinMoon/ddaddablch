package com.donation.ddb.Dto.Response;
import com.donation.ddb.Domain.Notification;
import com.donation.ddb.Domain.WalletAuthStatus;
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

    // 기본 프로필 정보
    private Long oId;
    private String oName;
    private String oEmail;
    private String oBusinessNumber;
    private String oDescription;
    private String oProfileImage;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // 캠페인 관련 정보
//    private Integer totalCampaignCount; // 총 캠페인 수
//    private Integer activeCampaignCount; // 진행 중인 캠페인 수
//    private Integer completedCampaignCount; // 완료된 캠페인 수
    private BigDecimal totalRaisedAmount; // 총 모금액
    private List<CampaignSummaryDTO> recentCampaigns; // 최근 캠페인 목록
    //기부중이랑 완료는 나눠서 보여주기
    //캠페인 -> 이름 단체명 설명 현재기부금액 목표금액 이미지.


    // 기부 관련 통계
    //private Integer totalDonorCount; // 총 기부자 수
   // private BigDecimal averageDonationAmount; // 평균 기부액
    //private List<DonationReceivedDTO> recentDonations; // 최근 받은 기부 내역

    // 활동 정보
    //private List<CampaignUpdateDTO> recentUpdates; // 최근 캠페인 업데이트
    //private List<CampaignCommentDTO> recentComments; // 최근 댓글

    // 알림 정보
    //private List<Notification> unreadNotifications; // 읽지 않은 알림

    // 내부 DTO 클래스들
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CampaignSummaryDTO {
        private Long campaignId;
        private String cTitle;
        private String cDescription;
        private BigDecimal cTargetAmount;
        private BigDecimal cCurrentAmount;
        private Integer cDonationCount;
        private String cStatus;
        private String cImageUrl;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime cStartDate;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime cEndDate;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        // 달성률 계산
        public BigDecimal getAchievementRate() {
            if (cTargetAmount == null || cTargetAmount.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            }
            return cCurrentAmount.divide(cTargetAmount, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonationReceivedDTO {
        private Long donationId;
        private String donorName; // 기부자 이름 (익명 처리 가능)
        private String campaignName;
        private BigDecimal donationAmount;
        private String transactionHash;
        private String donationStatus;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime donationDate;

        private Boolean isAnonymous; // 익명 기부 여부
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CampaignUpdateDTO {
        private Long updateId;
        private Long campaignId;
        private String campaignName;
        private String updateTitle;
        private String updateContent;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        private String updateType; // "PROGRESS", "COMPLETION", "NOTICE" 등
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CampaignCommentDTO {
        private Long ccId;
        private String ccContent;
        private Long campaignId;
        private String campaignName;
        private String commenterName;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        private Long commentLikeCount;
        private Boolean isReported; // 신고된 댓글 여부
    }

//    // 편의 메소드들
//    public BigDecimal getCampaignSuccessRate() {
//        if (totalCampaignCount == null || totalCampaignCount == 0) {
//            return BigDecimal.ZERO;
//        }
//        return BigDecimal.valueOf(completedCampaignCount)
//                .divide(BigDecimal.valueOf(totalCampaignCount), 4, BigDecimal.ROUND_HALF_UP)
//                .multiply(BigDecimal.valueOf(100));
//    }
}
