package com.donation.ddb.Repository.CampaignRepository;


import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.CampaignCategory;
import com.donation.ddb.Domain.CampaignSortType;
import com.donation.ddb.Domain.CampaignStatusFlag;
import com.donation.ddb.Dto.Response.CampaignResponse;
import com.donation.ddb.apiPayload.ApiResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface CampaignRepositoryCustom {
    List<CampaignResponse.CampaignListDto> dynamicQueryWithBooleanBuilder(
            String keyword,
            CampaignCategory category,
            CampaignStatusFlag statusFlag,
            CampaignSortType sortType,
            Pageable pageable
    );

    Campaign addCampaign(
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
