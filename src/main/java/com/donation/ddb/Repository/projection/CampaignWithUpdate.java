package com.donation.ddb.Repository.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CampaignWithUpdate {
    private Long campaignId;
    private String title;
    private String previewContent;
    private LocalDateTime createdAt;
}
