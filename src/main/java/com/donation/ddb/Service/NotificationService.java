package com.donation.ddb.Service;

import com.donation.ddb.Domain.Enums.NotificationType;
import com.donation.ddb.Domain.Exception.DataNotFoundException;
import com.donation.ddb.Domain.Notification;
import com.donation.ddb.Repository.NotificationRepository;
import com.donation.ddb.Repository.StudentUserRepository;
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
    private final StudentUserRepository studentUserRepository;

    // 읽지 않은 알림 개수 조회
    public Integer getUnreadNotificationCount(Long studentId) {
        if(!studentUserRepository.existsById(studentId)){
            throw new DataNotFoundException("해당 id의 학생이 존재하지 않습니다.");
        }
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
        int updatedRows = notificationRepository.markAsRead(notificationId);
        if (updatedRows == 0) {
            throw new DataNotFoundException("알림을 찾을 수 없습니다");
        }
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
                .redirectUrl(postId)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.info("댓글 알림 생성: 사용자 {} -> 게시글 {}", postAuthorId, postId);
    }

    // 🔥 새로운 메서드 - 캠페인 ID를 직접 받음 (권장)
    public void createDonationCompleteNotification(Long studentId, Long campaignId, String campaignName, Long donationId) {
        Notification notification = Notification.builder()
                .studentId(studentId)
                .title("기부가 완료되었습니다")
                .content(campaignName + " 캠페인에 기부가 성공적으로 완료되었습니다")
                .notificationType(NotificationType.DONATION_COMPLETE)
                .relatedDonationId(donationId)
                .redirectUrl(campaignId) // 🎯 ID 직접 사용!
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.info("기부 완료 알림 생성: 사용자 {} -> 캠페인 {} -> 기부 {}", studentId, campaignId, donationId);
    }

    // 기존 메서드 호환성 유지 (deprecated)
    @Deprecated
    public void createDonationCompleteNotification(Long studentId, String campaignName, Long donationId) {
        Long campaignId = null; // 🔥 미리 선언

        try {
            campaignId = campaignQueryService.findBycName(campaignName).getCId();
            createDonationCompleteNotification(studentId, campaignId, campaignName, donationId);
        } catch (Exception e) {
            log.warn("캠페인 조회 실패로 기본 알림 생성: {}", campaignName, e);

            Notification notification = Notification.builder()
                    .studentId(studentId)
                    .title("기부가 완료되었습니다")
                    .content(campaignName + " 캠페인에 기부가 성공적으로 완료되었습니다")
                    .notificationType(NotificationType.DONATION_COMPLETE)
                    .relatedDonationId(donationId)
                    .redirectUrl(campaignId) // 🔥 이제 campaignId 사용 가능! (null일 수 있음)
                    .isRead(false)
                    .build();

            notificationRepository.save(notification);
            log.info("기부 완료 알림 생성 (폴백): 사용자 {} -> 기부 {}", studentId, donationId);
        }
    }
    }
