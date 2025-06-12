package com.donation.ddb.Dto.Request;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgSubscriptionRequestDTO {
    private Long organizationId;
    private Boolean notificationEnabled = true; // 기본값 true
}
