package com.donation.ddb.Service.CampaignService;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.CampaignCategory;
import com.donation.ddb.Dto.Request.CampaignRequestDto;
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

    Campaign addCampaign(CampaignRequestDto.JoinDto joinDto, String email);

    Campaign findBycId(Long cId);

    Boolean existsBycId(Long cId);

    Campaign updateCampaign(Campaign campaign);


}
