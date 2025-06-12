package com.donation.ddb.Controller;

import com.donation.ddb.Domain.Exception.DataNotFoundException;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Request.NotificationSettingUpdateDTO;
import com.donation.ddb.Dto.Request.OrgSubscriptionRequestDTO;
import com.donation.ddb.Dto.Response.OrgSubscriptionResponseDTO;
import com.donation.ddb.Dto.Response.OrgSubscriptionStatsDTO;
import com.donation.ddb.apiPayload.ApiResponse;
import com.donation.ddb.apiPayload.code.status.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organizations/subscriptions")
@RequiredArgsConstructor
public class OrgSubscriptionController {

    private final com.donation.ddb.Service.OrgSubscriptionService.OrgSubscriptionService orgSubscriptionService;

    // 단체 구독
    @PostMapping("/subscribe/{studentId}")
    public ResponseEntity<OrgSubscriptionResponseDTO> subscribeToOrganization(
            @PathVariable(value="studentId") Long studentId,
            @RequestBody OrgSubscriptionRequestDTO requestDTO) {

        OrgSubscriptionResponseDTO response =
                orgSubscriptionService.subscribeToOrganization(studentId, requestDTO);
        return ResponseEntity.ok(response);
    }

    // 단체 구독 취소
    @DeleteMapping("/unsubscribe/{studentId}/{organizationId}")
    public ResponseEntity<?> unsubscribeFromOrganization(
            @PathVariable(value="studentId") Long studentId,
            @PathVariable(value="organizationId") Long organizationId) {
        orgSubscriptionService.unsubscribeFromOrganization(studentId, organizationId);
        return ResponseEntity.ok(
               Map.of("success","true",
                       "message","단체 구독이 취소되었습니다.")
        );
    }

    // 학생의 구독 단체 목록 조회
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<OrgSubscriptionResponseDTO>> getStudentSubscriptions(
            @PathVariable(value="studentId") Long studentId) {

        List<OrgSubscriptionResponseDTO> subscriptions =
                orgSubscriptionService.getStudentSubscriptions(studentId);
        return ResponseEntity.ok(subscriptions);
    }

    // 단체의 구독자 목록 조회
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<OrgSubscriptionResponseDTO>> getOrganizationSubscribers(
            @PathVariable(value="organizationId") Long organizationId) {

        List<OrgSubscriptionResponseDTO> subscribers =
                orgSubscriptionService.getOrganizationSubscribers(organizationId);
        return ResponseEntity.ok(subscribers);
    }

    // 구독 상태 확인
    @GetMapping("/status/{studentId}/{organizationId}")
    public ResponseEntity<Map<String, Boolean>> checkSubscriptionStatus(
            @PathVariable(value="studentId") Long studentId,
            @PathVariable(value="organizationId") Long organizationId) {

            boolean isSubscribed = orgSubscriptionService.isSubscribed(studentId, organizationId);
            return ResponseEntity.ok(Map.of("isSubscribed", isSubscribed));

    }

    // 단체 구독자 수 조회
    @GetMapping("/count/{organizationId}")
    public ResponseEntity<Map<String, Long>> getOrganizationSubscriberCount(
            @PathVariable(value="organizationId") Long organizationId) {

        Long count = orgSubscriptionService.getOrganizationSubscriberCount(organizationId);
        return ResponseEntity.ok(Map.of("subscriberCount", count));
    }

    // 알림 설정 업데이트
    @PutMapping("/notification/{studentId}")
    public ResponseEntity<Void> updateNotificationSetting(
            @PathVariable(value="studentId") Long studentId,
            @RequestBody NotificationSettingUpdateDTO updateDTO) {

        orgSubscriptionService.updateNotificationSetting(studentId, updateDTO);
        return ResponseEntity.ok().build();
    }

    // 인기 단체 목록 조회 (구독자 수 기준)
    @GetMapping("/popular")
    public ResponseEntity<List<OrgSubscriptionStatsDTO>> getPopularOrganizations() {
        List<OrgSubscriptionStatsDTO> popularOrgs = orgSubscriptionService.getPopularOrganizations();
        return ResponseEntity.ok(popularOrgs);
    }

    // 새 캠페인 알림 대상자 조회 (단체가 새 캠페인 생성 시 사용)
    @GetMapping("/notification-targets/{organizationId}")
    public ResponseEntity<List<StudentUser>> getNotificationTargets(
            @PathVariable(value="organizationId") Long organizationId) {

        List<StudentUser> targets = orgSubscriptionService.getNotificationTargets(organizationId);
        return ResponseEntity.ok(targets);
    }
}