package com.donation.ddb.Dto.Response;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.CampaignCategory;
import com.donation.ddb.Domain.CampaignStatusFlag;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignResponseDto {
    private Long cId;
    private String cName;
    private String cImageUrl;
    private String cDescription;
    private Integer cGoal;
    private Integer cCurrentAmount = 0;
    private CampaignCategory cCategory = CampaignCategory.NAME1;
    private Long donateCount = 0L;
    private LocalDate donateStart;
    private LocalDate donateEnd;
    private LocalDate businessStart;
    private LocalDate businessEnd;
    private CampaignStatusFlag cStatusFlag;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

//    @Builder
//    @Getter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class CampaignResponseDTO{
//        String testString;
//    }
//
//    @Builder
//    @Getter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class CampaignExceptionDTO{
//        Integer flag;
//    }
}
