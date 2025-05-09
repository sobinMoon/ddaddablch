package com.donation.ddb.Repository.CampaignRepository;


import com.donation.ddb.Domain.CampaignCategory;
import com.donation.ddb.Domain.CampaignSortType;
import com.donation.ddb.Domain.CampaignStatusFlag;
import com.donation.ddb.Dto.Response.CampaignResponseDto;
import com.donation.ddb.apiPayload.ApiResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface CampaignRepositoryCustom {
    List<CampaignResponseDto> dynamicQueryWithBooleanBuilder(
            String keyword,
            CampaignCategory category,
            CampaignStatusFlag statusFlag,
            CampaignSortType sortType,
            Pageable pageable
    );

    CampaignResponseDto addCampaign(
            Long oId,
            String cName,
            String CImageUrl,
            String cDescription,
            Integer cGoal,
            CampaignCategory cCategory,
            LocalDate donateStart,
            LocalDate donateEnd,
            LocalDate businessStart,
            LocalDate businessEnd
    );
}
