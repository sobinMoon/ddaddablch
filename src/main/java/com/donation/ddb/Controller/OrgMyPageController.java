package com.donation.ddb.Controller;

import com.donation.ddb.Dto.Response.OrgMyPageResponseDTO;
import com.donation.ddb.Service.MyPageService.OrgMyPageService;
import com.donation.ddb.apiPayload.ApiResponse;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;



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
}
