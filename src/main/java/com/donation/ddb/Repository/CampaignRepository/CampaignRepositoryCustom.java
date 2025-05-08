package com.donation.ddb.Repository.CampaignRepository;


import com.donation.ddb.Domain.CampaignCategory;
import com.donation.ddb.Domain.CampaignSortType;
import com.donation.ddb.Domain.CampaignStatusFlag;
import com.donation.ddb.Dto.Response.CampaignResponseDto;
import org.springframework.data.domain.Pageable;

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

    );
}
