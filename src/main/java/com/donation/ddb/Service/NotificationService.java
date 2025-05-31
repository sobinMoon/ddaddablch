package com.donation.ddb.Service;

import com.donation.ddb.Domain.Enums.NotificationType;
import com.donation.ddb.Domain.Notification;
import com.donation.ddb.Repository.NotificationRepository;
import com.donation.ddb.Service.CampaignCommentQueryService.CampaignCommentQueryService;
import com.donation.ddb.Service.CampaignService.CampaignQueryServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CampaignQueryServiceImpl campaignQueryService;

    // 읽지 않은 알림 개수 조회
    public Integer getUnreadNotificationCount(Long studentId) {
        return notificationRepository.countByStudentIdAndIsReadFalse(studentId);
    }

    // 읽지 않은 알림 목록 조회
    public List<Notification> getUnreadNotifications(Long studentId) {
        return notificationRepository.findByStudentIdAndIsReadFalseOrderByCreatedAtDesc(studentId);
    }

    // 모든 알림 목록 조회
    public List<Notification> getAllNotifications(Long studentId) {
        return notificationRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    // 알림 읽음 처리
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    // 모든 알림 읽음 처리
    @Transactional
    public void markAllAsRead(Long studentId) {
        notificationRepository.markAllAsReadByStudentId(studentId);
    }

    // 댓글 알림 생성
    public void createCommentNotification(Long postAuthorId, String commenterName, Long postId) {
        Notification notification = Notification.builder()
                .studentId(postAuthorId)
                .title("새 댓글이 달렸습니다")
                .content(commenterName + "님이 회원님의 게시글에 댓글을 달았습니다")
                .notificationType(NotificationType.POST_COMMENT)
                .relatedPostId(postId)
                .redirectUrl("api/v1/posts/" + postId)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.info("댓글 알림 생성: 사용자 {} -> 게시글 {}", postAuthorId, postId);
    }

    // 기부 완료 알림 생성
    public void createDonationCompleteNotification(Long studentId, String campaignName, Long donationId) {
        Notification notification = Notification.builder()
                .studentId(studentId)
                .title("기부가 완료되었습니다")
                .content(campaignName + " 캠페인에 기부가 성공적으로 완료되었습니다")
                .notificationType(NotificationType.DONATION_COMPLETE)
                .relatedDonationId(donationId)
                .redirectUrl("/api/v1/campaigns/"+campaignQueryService.findBycName(campaignName).getCId())
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.info("기부 완료 알림 생성: 사용자 {} -> 기부 {}", studentId, donationId);
    }
}