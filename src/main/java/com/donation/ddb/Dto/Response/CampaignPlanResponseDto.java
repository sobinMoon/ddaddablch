package com.donation.ddb.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignPlanResponseDto {
    private Long id;
    private String title;
    private Integer amount;
}
