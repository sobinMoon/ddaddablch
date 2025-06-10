package com.donation.ddb.Service.MyPageService;


import com.donation.ddb.Domain.Enums.CampaignStatusFlag;
import com.donation.ddb.Dto.Request.OrgInfoUpdatePwdDTO;
import com.donation.ddb.Dto.Request.OrgInfoUpdateRequestDTO;
import com.donation.ddb.ImageStore;
import com.donation.ddb.Repository.CampaignRepository.CampaignRepository;
import com.donation.ddb.Repository.OrganizationUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.donation.ddb.Domain.OrganizationUser;
import com.donation.ddb.Dto.Response.OrgMyPageResponseDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import java.math.BigDecimal;
import java.util.List;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrgMyPageService {
    private final OrganizationUserRepository organizationUserRepository;
    private final CampaignRepository campaignRepository;
    private final PasswordEncoder passwordEncoder;

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
                .oBusinessNumber(organization.getOBusinessNumber())
                .completedCampaigns(completedCampaigns)
                .activeCampaigns(activeCampaigns)
                .fundraisingCampaigns(fundraisingCampaigns)
                .build();

        log.info("조직 마이페이지 정보 조회 완료: 조직ID={}, 총모금액={}",
                organization.getOId(), totalRaisedAmount);

        return response;
    }

    // 조직 설명 + 이미지 업데이트 (비밀번호 확인 필요)
    @Transactional
    public String updateOrganizationInfo(OrgInfoUpdateRequestDTO updateDto, MultipartFile profileImage) {
        OrganizationUser organization = getCurrentOrganization();
        boolean hasChanges = false;
        log.info("조직 정보 업데이트 시작: 조직ID={}", organization.getOId());

        // 현재 비밀번호 확인 (설명 변경시 필수)
        if (updateDto != null && StringUtils.hasText(updateDto.getCurrentPassword())) {
            if (!passwordEncoder.matches(updateDto.getCurrentPassword(), organization.getOPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            log.info("비밀번호 확인 완료: 조직ID={}", organization.getOId());
        } else if (updateDto != null && StringUtils.hasText(updateDto.getDescription())) {
            throw new IllegalArgumentException("조직 설명 변경을 위해 현재 비밀번호를 입력해주세요.");
        }

        // 조직 설명 수정
        if (updateDto != null && updateDto.getDescription() != null) {
            String newDescription = updateDto.getDescription().trim();
            log.info("조직 설명 업데이트 시도: 조직ID={}", organization.getOId());

            // 현재 설명과 다른 경우에만 업데이트
            if (!newDescription.equals(organization.getODescription())) {
                organization.setODescription(newDescription);
                hasChanges = true;
                log.info("조직 설명 업데이트 완료: 조직ID={}", organization.getOId());
            } else {
                log.info("조직 설명이 기존과 동일함: 조직ID={}", organization.getOId());
            }
        }

        // 프로필 이미지 수정
        if (profileImage != null && !profileImage.isEmpty()) {
            updateProfileImageInternal(organization, profileImage);
            hasChanges = true;
        }

        if (hasChanges) {
            organizationUserRepository.save(organization);
            return "조직 정보가 성공적으로 업데이트되었습니다.";
        } else {
            return "변경사항이 없습니다.";
        }
    }

    // 조직 비밀번호 변경 전용 메서드
    @Transactional
    public String updateOrganizationPassword(OrgInfoUpdatePwdDTO updateDto) {
        OrganizationUser organization = getCurrentOrganization();
        boolean hasChanges = false;
        log.info("조직 비밀번호 업데이트 시작: 조직ID={}", organization.getOId());

        // 비밀번호 수정
        if (updateDto != null && StringUtils.hasText(updateDto.getCurrentPassword()) &&
                StringUtils.hasText(updateDto.getNewPassword())) {
            updatePasswordInternal(organization, updateDto.getCurrentPassword(),
                    updateDto.getNewPassword(), updateDto.getConfirmNewPassword());
            hasChanges = true;
        }



        if (hasChanges) {
            organizationUserRepository.save(organization);
            return "조직 정보가 성공적으로 업데이트되었습니다.";
        } else {
            return "변경사항이 없습니다.";
        }
    }

    // 현재 로그인한 조직 정보 조회
    private OrganizationUser getCurrentOrganization() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        String currentUserEmail = authentication.getName();
        return organizationUserRepository.findByoEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalStateException("로그인된 조직을 찾을 수 없습니다."));
    }

    // 비밀번호 업데이트 (내부 로직)
    private void updatePasswordInternal(OrganizationUser organization, String currentPassword,
                                        String newPassword, String confirmNewPassword) {
        // 입력값 검증
        if (!StringUtils.hasText(currentPassword)) {
            throw new IllegalArgumentException("현재 비밀번호를 입력해주세요.");
        }
        if (!StringUtils.hasText(newPassword)) {
            throw new IllegalArgumentException("새 비밀번호를 입력해주세요.");
        }
        if (!StringUtils.hasText(confirmNewPassword)) {
            throw new IllegalArgumentException("새 비밀번호 확인을 입력해주세요.");
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, organization.getOPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 확인
        if (!newPassword.equals(confirmNewPassword)) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        // 현재 비밀번호와 새 비밀번호가 같은지 확인
        if (passwordEncoder.matches(newPassword, organization.getOPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        // 비밀번호 암호화 후 저장
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        organization.setOPassword(encodedNewPassword);

        log.info("조직 비밀번호 업데이트 완료: 조직ID={}", organization.getOId());
    }

    // 프로필 이미지 업데이트 (내부 로직)
    private void updateProfileImageInternal(OrganizationUser organization, MultipartFile profileImage) {
        try {
            // 이미지 파일 검증
            if (profileImage.getSize() > 5 * 1024 * 1024) { // 5MB 제한
                throw new IllegalArgumentException("이미지 파일 크기는 5MB를 초과할 수 없습니다.");
            }

            String contentType = profileImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
            }

            // 기존 이미지 삭제 (기본 이미지가 아닌 경우에만)
            String currentImage = organization.getOProfileImage();
            if (currentImage != null && !currentImage.contains("default_profile.png")) {
                ImageStore.deleteImage(currentImage);
            }

            // 새 이미지 저장 (플랫폼 독립적 경로)
            String imagePath = ImageStore.storeImage(profileImage,
                    "/organizations/" + organization.getOId() + "/");
            organization.setOProfileImage(imagePath);

            log.info("조직 프로필 이미지 업데이트 완료: 조직ID={}, 이미지경로={}",
                    organization.getOId(), imagePath);

        } catch (IllegalArgumentException e) {
            throw e; // 검증 오류는 그대로 전달
        } catch (Exception e) {
            log.error("조직 프로필 이미지 업데이트 실패", e);
            throw new RuntimeException("프로필 이미지 업데이트에 실패했습니다.", e);
        }
    }
}
