package com.donation.ddb.Service.CampaignService;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Dto.Response.CampaignResponse;
import com.donation.ddb.Repository.projection.CampaignWithUpdate;

import java.util.List;

public interface CampaignQueryService {

    List<CampaignResponse.CampaignListDto> findAllCampaigns(
            String keyword,
            String category,
            String statusFlag,
            String sortType,
            Integer size
    );

    List<CampaignWithUpdate> findRecentUpdates();

    Campaign findBycId(Long cId);

    Boolean existsBycId(Long cId);

    Campaign findBycName(String cName);

}
