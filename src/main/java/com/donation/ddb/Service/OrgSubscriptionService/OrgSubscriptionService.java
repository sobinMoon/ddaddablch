package com.donation.ddb.Service.OrgSubscriptionService;

import com.donation.ddb.Domain.*;
import com.donation.ddb.Domain.Enums.CampaignStatusFlag;
import com.donation.ddb.Dto.Request.NotificationSettingUpdateDTO;
import com.donation.ddb.Dto.Request.OrgSubscriptionRequestDTO;
import com.donation.ddb.Dto.Response.OrgSubscriptionResponseDTO;
import com.donation.ddb.Dto.Response.OrgSubscriptionStatsDTO;
import com.donation.ddb.Repository.OrgSubscriptionRepository;

import com.donation.ddb.Repository.OrganizationUserRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrgSubscriptionService {

    private final OrgSubscriptionRepository subscriptionRepository;
    private final OrganizationUserRepository organizationUserRepository;
    private final StudentUserRepository studentUserRepository;
    private final com.donation.ddb.Repository.CampaignRepository.CampaignRepository campaignRepository;

    // 단체 구독
    public OrgSubscriptionResponseDTO subscribeToOrganization(Long studentId, OrgSubscriptionRequestDTO requestDTO) {
        StudentUser student = studentUserRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("학생을 찾을 수 없습니다."));

        OrganizationUser organization = organizationUserRepository.findById(requestDTO.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("단체를 찾을 수 없습니다."));

        // 이미 구독 중인지 확인
        Optional<OrgSubscription> existingSubscription =
                subscriptionRepository.findByOrganizationUserAndStudentUser(organization, student);

        OrgSubscription subscription;
        if (existingSubscription.isPresent()) {
            // 기존 구독이 있으면 활성화
            subscription = existingSubscription.get();
            subscription.setIsActive(true);
            subscription.setNotificationEnabled(requestDTO.getNotificationEnabled());
        } else {
            // 새로운 구독 생성
            subscription = OrgSubscription.builder()
                    .organizationUser(organization)
                    .studentUser(student)
                    .isActive(true)
                    .notificationEnabled(requestDTO.getNotificationEnabled())
                    .build();
        }

        subscription = subscriptionRepository.save(subscription);
        log.info("사용자 {}가 단체 {}를 구독했습니다.", studentId, requestDTO.getOrganizationId());

        return convertToResponseDTO(subscription);
    }

    // 단체 구독 취소
    public void unsubscribeFromOrganization(Long studentId, Long organizationId) {
        StudentUser student = studentUserRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("학생을 찾을 수 없습니다."));

        OrganizationUser organization = organizationUserRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("단체를 찾을 수 없습니다."));

        OrgSubscription subscription = subscriptionRepository.findByOrganizationUserAndStudentUser(organization, student)
                .orElseThrow(() -> new RuntimeException("구독 정보를 찾을 수 없습니다."));

        subscription.setIsActive(false);
        subscriptionRepository.save(subscription);

        log.info("사용자 {}가 단체 {} 구독을 취소했습니다.", studentId, organizationId);
    }

    // 학생의 구독 단체 목록 조회
    @Transactional(readOnly = true)
    public List<OrgSubscriptionResponseDTO> getStudentSubscriptions(Long studentId) {
        StudentUser student = studentUserRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("학생을 찾을 수 없습니다."));

        List<OrgSubscription> subscriptions =
                subscriptionRepository.findByStudentUserAndIsActiveTrue(student);

        return subscriptions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // 단체의 구독자 목록 조회
    @Transactional(readOnly = true)
    public List<OrgSubscriptionResponseDTO> getOrganizationSubscribers(Long organizationId) {
        OrganizationUser organization = organizationUserRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("단체를 찾을 수 없습니다."));

        List<OrgSubscription> subscriptions =
                subscriptionRepository.findByOrganizationUserAndIsActiveTrue(organization);

        return subscriptions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // 구독 상태 확인
    @Transactional(readOnly = true)
    public boolean isSubscribed(Long studentId, Long organizationId) {
        StudentUser student = studentUserRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("학생을 찾을 수 없습니다."));

        OrganizationUser organization = organizationUserRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("단체를 찾을 수 없습니다."));

        Optional<OrgSubscription> subscription =
                subscriptionRepository.findByOrganizationUserAndStudentUser(organization, student);

        return subscription.isPresent() && subscription.get().getIsActive();
    }

    // 단체 구독자 수 조회
    @Transactional(readOnly = true)
    public Long getOrganizationSubscriberCount(Long organizationId) {
        OrganizationUser organization = organizationUserRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("단체를 찾을 수 없습니다."));

        return subscriptionRepository.countByOrganizationUserAndIsActiveTrue(organization);
    }

    // 알림 설정 업데이트
    public void updateNotificationSetting(Long studentId, NotificationSettingUpdateDTO updateDTO) {
        StudentUser student = studentUserRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("학생을 찾을 수 없습니다."));

        OrganizationUser organization = organizationUserRepository.findById(updateDTO.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("단체를 찾을 수 없습니다."));

        OrgSubscription subscription = subscriptionRepository.findByOrganizationUserAndStudentUser(organization, student)
                .orElseThrow(() -> new RuntimeException("구독 정보를 찾을 수 없습니다."));

        subscription.setNotificationEnabled(updateDTO.getNotificationEnabled());
        subscriptionRepository.save(subscription);

        log.info("사용자 {}의 단체 {} 알림 설정을 {}로 변경했습니다.",
                studentId, updateDTO.getOrganizationId(), updateDTO.getNotificationEnabled());
    }

    // 인기 단체 조회 (구독자 수 기준)
    @Transactional(readOnly = true)
    public List<OrgSubscriptionStatsDTO> getPopularOrganizations() {
        List<Object[]> results = subscriptionRepository.findPopularOrganizations();

        return results.stream()
                .map(result -> {
                    OrganizationUser org = (OrganizationUser) result[0];
                    Long subscriberCount = (Long) result[1];

                    // 해당 단체의 캠페인 통계
                    Long totalCampaigns = (long) org.getCampaigns().size();
                    Long activeCampaigns = org.getCampaigns().stream()
                            .filter(campaign -> campaign.getCStatusFlag() == CampaignStatusFlag.FUNDRAISING)
                            .count();

                    return OrgSubscriptionStatsDTO.builder()
                            .organizationId(org.getOId())
                            .organizationName(org.getOName())
                            .subscriberCount(subscriberCount)
                            .totalCampaignCount(totalCampaigns)
                            .activeCampaignCount(activeCampaigns)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 새 캠페인 알림 전송 대상자 조회 (단체가 새 캠페인을 만들 때 사용)
    @Transactional(readOnly = true)
    public List<StudentUser> getNotificationTargets(Long organizationId) {
        OrganizationUser organization = organizationUserRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("단체를 찾을 수 없습니다."));

        List<OrgSubscription> subscribers =
                subscriptionRepository.findNotificationEnabledSubscribers(organization);

        return subscribers.stream()
                .map(OrgSubscription::getStudentUser)
                .collect(Collectors.toList());
    }

    private OrgSubscriptionResponseDTO convertToResponseDTO(OrgSubscription subscription) {
        OrganizationUser org = subscription.getOrganizationUser();

        // 캠페인 통계 계산
        int totalCampaigns = org.getCampaigns().size();
        int activeCampaigns = (int) org.getCampaigns().stream()
                .filter(campaign -> campaign.getCStatusFlag() == CampaignStatusFlag.FUNDRAISING)
                .count();

        return OrgSubscriptionResponseDTO.builder()
                .subscriptionId(subscription.getOsId())
                .organizationId(org.getOId())
                .organizationName(org.getOName())
                .organizationEmail(org.getOEmail())
                .organizationDescription(org.getODescription())
                .organizationProfileImage(org.getOProfileImage())
                .businessNumber(org.getOBusinessNumber())
                .studentId(subscription.getStudentUser().getSId())
                .studentName(subscription.getStudentUser().getSName())
                .isActive(subscription.getIsActive())
                .notificationEnabled(subscription.getNotificationEnabled())
                .totalCampaignCount(totalCampaigns)
                .activeCampaignCount(activeCampaigns)
                .subscribedAt(subscription.getUpdatedAt())
                .build();
    }
}