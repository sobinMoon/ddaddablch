package com.donation.ddb.Dto.Response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentMyPageResponseDTO {

    // 기본 프로필 정보
    private Long sId;
    private String sName;
    private String sNickname;
    private String sEmail;
    private String sProfileImage;
    private String sWalletAddress;
    private String sWalletAuthStatus;
    private LocalDateTime createdAt;

    //기부 관련 정보
    private BigDecimal totalDonationAmount; //총 기부 금액 (이더)
    private Integer totalDonationCount; //총 기부 횟수
    //private List<DonationHistoryDTO> recentDonations; //최근 기부 내역

    //활동 정보
    private Integer totalPostCound; //총 작성 글 수
    private Integer totalCommentCount; //총 댓글 수
    private Integer totalLikeCount; //받은 좋아요 수
    private List<RecentPostDTO> recentPosts; //최근 작성 글

    // 알림 정보
    private Integer unreadNotificationCount; // 읽지 않은 알림 수

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonationHistoryDTO {
        private Long donationId;
        private String campaignName;
        private BigDecimal donationAmount;
        private LocalDateTime donationDate;
        private String transactionHash;
        private String donationStatus;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentPostDTO {
        private Long postId;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private Integer likeCount;
        private Integer commentCount;
    }




}
