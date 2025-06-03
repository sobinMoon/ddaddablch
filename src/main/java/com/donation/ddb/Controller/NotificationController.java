package com.donation.ddb.Controller;

import com.donation.ddb.Domain.Exception.DataNotFoundException;
import com.donation.ddb.Service.NotificationService;
import com.donation.ddb.apiPayload.ApiResponse;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping("/unread/user")
    public ResponseEntity<?> getUnreadNoti(@RequestParam(value="sid") Long sid) {
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류 발생");
        }
    }

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "서버 오류"
                    ));
        }
    }
}