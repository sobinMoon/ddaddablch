package com.donation.ddb.Dto.Response;

import com.donation.ddb.Domain.CampaignComment;
import com.donation.ddb.Domain.Notification;
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
public class StudentMyPageResponseDTO {

    // 기본 프로필 정보
    private Long sId;
    private String sName;
    private String sNickname;
    private String sEmail;
    private String sProfileImage;
    private List<String> walletAddresses; //JSON으로 저장된 지갑 목록

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    //기부 관련 정보
    private BigDecimal totalDonationAmount; //총 기부 금액 (이더)
    private Integer totalDonationCount; //총 기부 횟수
    private List<DonationSummaryDTO> recentDonations; //최근 기부 내역 -> 그냥 전부 다

    // 활동 정보
    private List<RecentPostDTO> recentPosts; //최근 작성 글
    private List<PostCommentDTO> recentComments; //최근 댓글 모음

    // 알림 정보
    private List<Notification> unreadNotifications; // 읽지 않은 알림 수

    // nft 내역
    private List<String> nftImageUrls;          // 단순 URL 리스트

    // 내부 DTO 클래스들
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonationSummaryDTO {
        private Long donationId;
        private String campaignName;
        private Long campaignId;
        private BigDecimal donationAmount;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime donationDate;
        private String transactionHash;
        private String donationStatus;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentPostDTO{
        private Long postId;
        private String title;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
        private String pNFT;
        private Long likeCount;
        private Long commentCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostCommentDTO{
        private Long pcId;
        private String pcContent;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
        private Long postId;
        private Long commentLIkeCount;
    }

}
