package com.donation.ddb.Service.CampaignService;

import com.donation.ddb.Domain.CampaignCategory;
import com.donation.ddb.Domain.CampaignSortType;
import com.donation.ddb.Domain.CampaignStatusFlag;
import com.donation.ddb.Dto.Response.CampaignResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CampaignQueryService {

    List<CampaignResponseDto> findAllCampaigns(
            String keyword,
            String category,
            String statusFlag,
            String sortType,
            Pageable pageable
//            CampaignSearchDto searchDto
    );
}
