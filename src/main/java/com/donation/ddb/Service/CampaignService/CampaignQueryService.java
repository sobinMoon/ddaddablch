package com.donation.ddb.Service.CampaignService;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.CampaignCategory;
import com.donation.ddb.Domain.CampaignSortType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CampaignQueryService {

    List<Campaign> findAllCampaigns(
            String keyword,
            CampaignCategory category,
            CampaignSortType sortType,
            Pageable pageable
    );
}
