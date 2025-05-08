package com.donation.ddb.Repository.CampaignRepository;


import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.CampaignCategory;
import com.donation.ddb.Domain.CampaignSortType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface CampaignRepositoryCustom {
    List<Campaign> dynamicQueryWithBooleanBuilder(
            String keyword,
            CampaignCategory category,
            CampaignSortType sortType,
            Pageable pageable
    );
}
