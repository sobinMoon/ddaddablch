package com.donation.ddb.Dto.Response;
import lombok.*;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgSubscriptionStatsDTO {
    private Long organizationId;
    private String organizationName;
    private Long subscriberCount; // 구독자 수
    private Long totalCampaignCount; // 총 캠페인 수
    private Long activeCampaignCount; // 진행 중인 캠페인 수
}
