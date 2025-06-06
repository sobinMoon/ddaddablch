package com.donation.ddb.Repository.CampaignRepository;


import com.donation.ddb.Domain.*;
import com.donation.ddb.Domain.Enums.CampaignCategory;
import com.donation.ddb.Domain.Enums.CampaignSortType;
import com.donation.ddb.Domain.Enums.CampaignStatusFlag;
import com.donation.ddb.Dto.Request.CampaignRequestDto;
import com.donation.ddb.Dto.Response.CampaignResponse;

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
