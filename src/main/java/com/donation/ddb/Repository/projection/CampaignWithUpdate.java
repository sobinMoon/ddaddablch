package com.donation.ddb.Repository.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class CampaignWithUpdate {
    private Long campaignId;
    private String title;
    private String previewContent;
    private LocalDateTime createdAt;
}
