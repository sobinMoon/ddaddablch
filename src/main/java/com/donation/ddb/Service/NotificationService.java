package com.donation.ddb.Service;

import com.donation.ddb.Domain.Enums.NotificationType;
import com.donation.ddb.Domain.Exception.DataNotFoundException;
import com.donation.ddb.Domain.Notification;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Repository.NotificationRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import com.donation.ddb.Service.CampaignService.CampaignQueryServiceImpl;
import com.donation.ddb.Service.OrgSubscriptionService.OrgSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CampaignQueryServiceImpl campaignQueryService;
    private final StudentUserRepository studentUserRepository;
    private final OrgSubscriptionService orgSubscriptionService; // 필드 추가


    // 읽지 않은 알림 개수 조회
    public Integer getUnreadNotificationCount(Long studentId) {
        if (!studentUserRepository.existsById(studentId)) {
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

    // 새로운 메서드 - 캠페인 ID를 직접 받음 (권장)
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


    // 🔥 새 캠페인 알림 전송 (중복 제거된 버전)
    public void sendNewCampaignNotifications(Long organizationId, Long campaignId, String campaignName, String organizationName) {
        try {
            List<StudentUser> notificationTargets = orgSubscriptionService.getNotificationTargets(organizationId);

            log.info("새 캠페인 알림 전송 시작: 캠페인 '{}' -> {} 명의 구독자", campaignName, notificationTargets.size());

            for (StudentUser student : notificationTargets) {
                Notification notification = Notification.builder()
                        .studentId(student.getSId())
                        .title("새로운 캠페인이 시작되었습니다")
                        .content(organizationName + "에서 새로운 캠페인 '" + campaignName + "'을 시작했습니다")
                        .notificationType(NotificationType.NEW_CAMPAIGN) // 🔥 enum에 추가 필요
                        .redirectUrl(campaignId)
                        .isRead(false)
                        .build();

                notificationRepository.save(notification);
            }

            log.info("새 캠페인 알림 전송 완료: 총 {} 명에게 전송", notificationTargets.size());

        } catch (Exception e) {
            log.error("새 캠페인 알림 전송 중 오류 발생: 캠페인 '{}'", campaignName, e);
            throw e; // Controller에서 catch하여 처리
        }
    }

    // 🔥 캠페인 완료 알림 전송 (선택적)
    public void sendCampaignCompletedNotifications(Long organizationId, Long campaignId, String campaignName, String organizationName) {
        try {
            List<StudentUser> notificationTargets = orgSubscriptionService.getNotificationTargets(organizationId);

            log.info("캠페인 완료 알림 전송 시작: 캠페인 '{}' -> {} 명의 구독자", campaignName, notificationTargets.size());

            for (StudentUser student : notificationTargets) {
                Notification notification = Notification.builder()
                        .studentId(student.getSId())
                        .title("캠페인이 완료되었습니다")
                        .content(organizationName + "의 '" + campaignName + "' 캠페인이 성공적으로 완료되었습니다")
                        .notificationType(NotificationType.CAMPAIGN_COMPLETED) // 🔥 enum에 추가 필요
                        .redirectUrl(campaignId)
                        .isRead(false)
                        .build();

                notificationRepository.save(notification);
            }

            log.info("캠페인 완료 알림 전송 완료: 총 {} 명에게 전송", notificationTargets.size());

        } catch (Exception e) {
            log.error("캠페인 완료 알림 전송 중 오류 발생: 캠페인 '{}'", campaignName, e);
            throw e;
        }
    }
    /**
     * 🔥 구독자 목록 확인 테스트 (디버깅용)
     */
    @GetMapping("/test/subscribers/{organizationId}")
    public ResponseEntity<?> testGetSubscribers(@PathVariable Long organizationId) {
        try {
            log.info("🧪 구독자 조회 테스트 - 단체ID: {}", organizationId);

            // OrgSubscriptionService 직접 호출 (의존성 주입 필요)
            // 임시로 NotificationService를 통해 호출

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "구독자 조회 테스트 - 로그 확인하세요",
                    "organizationId", organizationId
            ));
        } catch (Exception e) {
            log.error("🚨 구독자 조회 테스트 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "구독자 조회 실패: " + e.getMessage()
                    ));
        }
    }

    /**
     * 🔥 알림 생성 기본 테스트 (가장 간단한 테스트)
     */
    @PostMapping("/test/simple")
    public ResponseEntity<?> testSimpleNotification(@RequestBody Map<String, Object> request) {
        try {
            log.info("🧪 간단한 알림 테스트 시작");

            Long studentId = Long.valueOf(request.get("studentId").toString());
            String message = request.get("message").toString();

            // 🔥 간단한 알림 직접 생성 (NotificationType 문제 확인용)
            Notification notification = Notification.builder()
                    .studentId(studentId)
                    .title("테스트 알림")
                    .content(message)
                    .notificationType(NotificationType.NEW_CAMPAIGN) // 🔥 여기서 오류 날 수 있음
                    .redirectUrl(999L)
                    .isRead(false)
                    .build();

            // NotificationRepository 직접 저장 (의존성 주입 필요)
            // 임시로 NotificationService 사용

            log.info("✅ 간단한 알림 테스트 성공");
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "간단한 알림 테스트 성공!"
            ));
        } catch (Exception e) {
            log.error("🚨 간단한 알림 테스트 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "NotificationType.NEW_CAMPAIGN 오류 가능성: " + e.getMessage()
                    ));
        }
    }
}
