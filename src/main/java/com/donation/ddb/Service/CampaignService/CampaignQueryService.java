package com.donation.ddb.Service.CampaignService;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.CampaignCategory;
import com.donation.ddb.Dto.Response.CampaignResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface CampaignQueryService {

    List<CampaignResponse.CampaignListDto> findAllCampaigns(
            String keyword,
            String category,
            String statusFlag,
            String sortType,
            Pageable pageable
//            CampaignSearchDto searchDto
    );

    Campaign addCampaign(
            Long oId,
            String cName,
            String cImageUrl,
            String cDescription,
            Integer cGoal,
            CampaignCategory cCategory,
            LocalDate donateStart,
            LocalDate donateEnd,
            LocalDate businessStart,
            LocalDate businessEnd
    );


}
