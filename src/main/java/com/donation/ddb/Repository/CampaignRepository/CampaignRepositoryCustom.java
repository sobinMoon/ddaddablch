package com.donation.ddb.Repository.CampaignRepository;


import com.donation.ddb.Domain.*;
import com.donation.ddb.Dto.Request.CampaignRequestDto;
import com.donation.ddb.Dto.Response.CampaignResponse;
import com.donation.ddb.apiPayload.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CampaignRepositoryCustom {
    List<CampaignResponse.CampaignListDto> dynamicQueryWithBooleanBuilder(
            String keyword,
            CampaignCategory category,
            CampaignStatusFlag statusFlag,
            CampaignSortType sortType
    );

    Campaign addCampaign(CampaignRequestDto.JoinDto joinDto, OrganizationUser organizationUser);
}
