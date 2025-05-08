package com.donation.ddb.Service.CampaignService;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.CampaignCategory;
import com.donation.ddb.Domain.CampaignSortType;
import com.donation.ddb.Repository.CampaignRepository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignQueryServiceImpl implements CampaignQueryService {

    private final CampaignRepository campaignRepository;

    @Override
    public List<Campaign> findAllCampaigns(String keyword, CampaignCategory category, CampaignSortType sortType, Pageable pageable) {
        List <Campaign> campaigns = campaignRepository.dynamicQueryWithBooleanBuilder(keyword, category, sortType, pageable);

        campaigns.forEach(campaign -> {
            System.out.println("Campaign " + campaign);
        });

        return campaigns;
    }
}
