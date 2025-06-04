package com.donation.ddb.Dto.Response;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgSubscriptionResponseDTO {
    private Long subscriptionId;
    private Long organizationId;
    private String organizationName;
    private String organizationEmail;
    private String organizationDescription;
    private String organizationProfileImage;
    private String businessNumber;
    private Long studentId;
    private String studentName;
    private Boolean isActive;
    private Boolean notificationEnabled;
    private Integer totalCampaignCount; // 해당 단체의 총 캠페인 수
    private Integer activeCampaignCount; // 진행 중인 캠페인 수

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime subscribedAt;
}
