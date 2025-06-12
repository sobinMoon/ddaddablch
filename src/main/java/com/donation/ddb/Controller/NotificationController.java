package com.donation.ddb.Controller;

import com.donation.ddb.Domain.Exception.DataNotFoundException;
import com.donation.ddb.Domain.Notification;
import com.donation.ddb.Service.NotificationService;
import com.donation.ddb.apiPayload.ApiResponse;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/notification")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 읽지 않은 알림 개수 조회
     */
    @GetMapping("/unread/count")
    public ResponseEntity<?> getUnreadNotificationCount(@RequestParam(value="sid") Long sid) {
        try {
            Integer count = notificationService.getUnreadNotificationCount(sid);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "읽지 않은 알림 개수 조회 완료",
                    "unreadCount", count
            ));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(ErrorStatus.STUDENT_USER_NOT_FOUND.getHttpStatus())
                    .body(ApiResponse.onFailure(
                            ErrorStatus.STUDENT_USER_NOT_FOUND.getCode(),
                            e.getMessage(),
                            null));
        } catch (Exception e) {
            log.error("읽지 않은 알림 개수 조회 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "서버 오류 발생"
                    ));
        }
    }

    /**
     * 읽지 않은 알림 목록 조회
     */
    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadNotifications(@RequestParam(value="sid") Long sid) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotifications(sid);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "읽지 않은 알림 목록 조회 완료",
                    "notifications", notifications,
                    "count", notifications.size()
            ));
        } catch (Exception e) {
            log.error("읽지 않은 알림 목록 조회 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "서버 오류 발생"
                    ));
        }
    }

    /**
     * 모든 알림 목록 조회
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllNotifications(@RequestParam(value="sid") Long sid) {
        try {
            List<Notification> notifications = notificationService.getAllNotifications(sid);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "전체 알림 목록 조회 완료",
                    "notifications", notifications,
                    "count", notifications.size()
            ));
        } catch (Exception e) {
            log.error("전체 알림 목록 조회 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "서버 오류 발생"
                    ));
        }
    }

    /**
     * 특정 알림 읽음 처리
     */
    @PutMapping("/read/{notificationId}")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable(value="notificationId") Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "알림이 읽음 처리되었습니다."
            ));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", "알림을 찾을 수 없습니다"
                    ));
        } catch (Exception e) {
            log.error("알림 읽음 처리 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "서버 오류"
                    ));
        }
    }


    //모든 알림 읽음 처리
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllNotificationsAsRead(@RequestParam(value="sid") Long sid) {
        try {
            notificationService.markAllAsRead(sid);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "모든 알림이 읽음 처리되었습니다."
            ));
        } catch (Exception e) {
            log.error("모든 알림 읽음 처리 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "서버 오류"
                    ));
        }
    }

    //
    @PostMapping("/comment")
    public ResponseEntity<?> createCommentNotification(
            @RequestBody Map<String, Object> request) {
        try {
            Long postAuthorId = Long.valueOf(request.get("postAuthorId").toString());
            String commenterName = request.get("commenterName").toString();
            Long postId = Long.valueOf(request.get("postId").toString());

            notificationService.createCommentNotification(postAuthorId, commenterName, postId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "댓글 알림이 생성되었습니다."
            ));
        } catch (Exception e) {
            log.error("댓글 알림 생성 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "서버 오류"
                    ));
        }
    }

    /**
     * 기부 완료 알림 생성 (테스트용 또는 관리자용)
     */
    @PostMapping("/donation")
    public ResponseEntity<?> createDonationCompleteNotification(
            @RequestBody Map<String, Object> request) {
        try {
            Long studentId = Long.valueOf(request.get("studentId").toString());
            String campaignName = request.get("campaignName").toString();
            Long donationId = Long.valueOf(request.get("donationId").toString());

            notificationService.createDonationCompleteNotification(studentId, campaignName, donationId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "기부 완료 알림이 생성되었습니다."
            ));
        } catch (Exception e) {
            log.error("기부 완료 알림 생성 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "서버 오류"
                    ));
        }
    }

    // 추가로 필요할 수 있는 엔드포인트들

    /**
     * 알림 삭제
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long notificationId) {
        try {
            // NotificationService에 deleteNotification 메서드가 있다면 사용
            // 현재는 없으므로 주석 처리
            /*
            notificationService.deleteNotification(notificationId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "알림이 삭제되었습니다."
            ));
            */

            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(Map.of(
                            "success", false,
                            "message", "아직 구현되지 않은 기능입니다."
                    ));
        } catch (Exception e) {
            log.error("알림 삭제 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "서버 오류"
                    ));
        }
    }

    /**
     * 특정 타입의 알림 조회
     */
    @GetMapping("/type/{notificationType}")
    public ResponseEntity<?> getNotificationsByType(
            @RequestParam(value="sid") Long sid,
            @PathVariable String notificationType) {
        try {
            // NotificationService에 해당 메서드가 있다면 사용
            // 현재는 없으므로 주석 처리
            /*
            List<Notification> notifications = notificationService.getNotificationsByType(sid, notificationType);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", notificationType + " 타입 알림 조회 완료",
                    "notifications", notifications
            ));
            */

            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(Map.of(
                            "success", false,
                            "message", "아직 구현되지 않은 기능입니다."
                    ));
        } catch (Exception e) {
            log.error("타입별 알림 조회 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "서버 오류"
                    ));
        }
    }
}