package com.donation.ddb.Dto.Request;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSettingUpdateDTO {
    private Long organizationId;
    private Boolean notificationEnabled;
}
