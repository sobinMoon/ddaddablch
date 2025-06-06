package com.donation.ddb.Service.MyPageService;


import com.donation.ddb.Domain.Enums.CampaignStatusFlag;
import com.donation.ddb.Repository.CampaignRepository.CampaignRepository;
import com.donation.ddb.Repository.OrganizationUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.donation.ddb.Domain.OrganizationUser;
import com.donation.ddb.Dto.Response.OrgMyPageResponseDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrgMyPageService {
    private final OrganizationUserRepository organizationUserRepository;
    private final CampaignRepository campaignRepository;

    // 토큰으로 현재 로그인한 조직의 마이페이지 조회
    public OrgMyPageResponseDTO getMyPageInfo() {
        try {
            // 현재 로그인한 조직 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();

            OrganizationUser organization = organizationUserRepository.findByoEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("조직을 찾을 수 없습니다."));

            return buildMyPageResponse(organization);

        } catch (Exception e) {
            log.error("조직 마이페이지 정보 조회 중 오류 발생", e);
            throw new RuntimeException("조직 마이페이지 정보를 불러오는데 실패했습니다.", e);
        }
    }

    // o_id로 특정 조직의 마이페이지 조회
    public OrgMyPageResponseDTO getMyPageInfoById(Long orgId) {
        try {
            OrganizationUser organization = organizationUserRepository.findById(orgId)
                    .orElseThrow(() -> new RuntimeException("조직을 찾을 수 없습니다. ID: " + orgId));

            return buildMyPageResponse(organization);

        } catch (Exception e) {
            log.error("조직 마이페이지 정보 조회 중 오류 발생 - orgId: {}", orgId, e);
            throw new RuntimeException("조직 마이페이지 정보를 불러오는데 실패했습니다.", e);
        }
    }

    // 공통 응답 생성 메서드
    private OrgMyPageResponseDTO buildMyPageResponse(OrganizationUser organization) {
        // 총 모금액 조회
        BigDecimal totalRaisedAmount = campaignRepository.getTotalRaisedAmountByOrganization(organization.getOId());

        // 상태별 캠페인 조회
        List<OrgMyPageResponseDTO.CampaignSummaryDTO> completedCampaigns =
                campaignRepository.getCampaignsByStatusAndOrganization(
                        organization.getOId(),
                        Arrays.asList(CampaignStatusFlag.COMPLETED)
                );

        List<OrgMyPageResponseDTO.CampaignSummaryDTO> activeCampaigns =
                campaignRepository.getCampaignsByStatusAndOrganization(
                        organization.getOId(),
                        Arrays.asList(CampaignStatusFlag.FUNDED, CampaignStatusFlag.IN_PROGRESS)
                );

        List<OrgMyPageResponseDTO.CampaignSummaryDTO> fundraisingCampaigns =
                campaignRepository.getCampaignsByStatusAndOrganization(
                        organization.getOId(),
                        Arrays.asList(CampaignStatusFlag.FUNDRAISING)
                );

        // 최종 응답 DTO 생성
        OrgMyPageResponseDTO response = OrgMyPageResponseDTO.builder()
                .oid(organization.getOId())
                .onName(organization.getOName())
                .oEmail(organization.getOEmail())
                .oDescription(organization.getODescription())
                .oProfileImage(organization.getOProfileImage())
                .createdAt(organization.getCreatedAt())
                .totalRaisedAmount(totalRaisedAmount)
                .completedCampaigns(completedCampaigns)
                .activeCampaigns(activeCampaigns)
                .fundraisingCampaigns(fundraisingCampaigns)
                .build();

        log.info("조직 마이페이지 정보 조회 완료: 조직ID={}, 총모금액={}",
                organization.getOId(), totalRaisedAmount);

        return response;
    }
}
