package com.donation.ddb.Controller;

import com.donation.ddb.Dto.Request.OrgInfoUpdatePwdDTO;
import com.donation.ddb.Dto.Request.OrgInfoUpdateRequestDTO;
import com.donation.ddb.Dto.Response.OrgMyPageResponseDTO;
import com.donation.ddb.Service.MyPageService.OrgMyPageService;
import com.donation.ddb.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@Slf4j
public class OrgMyPageController {

    private final OrgMyPageService organizationMyPageService;

    // 토큰으로 현재 로그인한 조직의 마이페이지 조회
    @GetMapping("/api/v1/mypage/org")
    public ApiResponse<OrgMyPageResponseDTO> getMyPageInfo() {
        try {
            OrgMyPageResponseDTO response = organizationMyPageService.getMyPageInfo();
            return ApiResponse.onSuccess(response);
        } catch (Exception e) {
            log.error("조직 마이페이지 조회 실패", e);
            return ApiResponse.onFailure("ORGANIZATION_MYPAGE_ERROR", "조직 마이페이지 정보를 불러올 수 없습니다.", null);
        }
    }

    // o_id로 특정 조직의 마이페이지 조회
    @GetMapping("/api/v1/mypage/org/{orgId}")
    public ApiResponse<OrgMyPageResponseDTO> getMyPageInfoById(@PathVariable(value="orgId") Long orgId) {
        try {
            OrgMyPageResponseDTO response = organizationMyPageService.getMyPageInfoById(orgId);
            return ApiResponse.onSuccess(response);
        } catch (Exception e) {
            log.error("조직 마이페이지 조회 실패 - orgId: {}", orgId, e);
            return ApiResponse.onFailure("ORGANIZATION_MYPAGE_ERROR", "조직 마이페이지 정보를 불러올 수 없습니다.", null);
        }
    }

    // 🔥 조직 정보 업데이트 (설명 + 이미지)
    @PutMapping("/api/v1/org/update")
    public ResponseEntity<String> updateOrganizationInfo(
            @Valid @RequestPart(value = "updateInfo", required = false) OrgInfoUpdateRequestDTO updateDto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        log.info("조직 정보 업데이트 요청: updateDto={}", updateDto);
        try {
            String result = organizationMyPageService.updateOrganizationInfo(updateDto, profileImage);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.error("조직 정보 업데이트 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("조직 정보 업데이트 중 오류 발생", e);
            return ResponseEntity.internalServerError().body("조직 정보 업데이트에 실패했습니다.");
        }
    }

    // 조직 비밀번호 업데이트
    @PutMapping("/api/v1/org/update/pwd")
    public ResponseEntity<String> updateOrganizationPassword(
            @Valid @RequestPart(value = "updateInfo", required = false) OrgInfoUpdatePwdDTO updateDto
          ) {
        log.info("조직 비밀번호 업데이트 요청");
        try {
            String result = organizationMyPageService.updateOrganizationPassword(updateDto);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.error("조직 비밀번호 업데이트 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("조직 비밀번호 업데이트 중 오류 발생", e);
            return ResponseEntity.internalServerError().body("비밀번호 업데이트에 실패했습니다.");
        }
    }
}
