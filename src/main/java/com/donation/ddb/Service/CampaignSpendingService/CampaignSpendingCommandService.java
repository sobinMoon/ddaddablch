package com.donation.ddb.Service.CampaignSpendingService;

import com.donation.ddb.Converter.CampaignPlanConverter;
import com.donation.ddb.Converter.CampaignSpendingConverter;
import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Dto.Request.CampaignPlanRequestDto;
import com.donation.ddb.Dto.Request.CampaignSpendingRequestDto;
import com.donation.ddb.Repository.CampaignSpendingRepository.CampaignSpendingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignSpendingCommandService {
    private final CampaignSpendingRepository campaignSpendingRepository;

    public void addCampaignPlan(List<CampaignSpendingRequestDto.JoinDto> spendings, Campaign campaign) {
        spendings.stream().
                map(plan -> CampaignSpendingConverter.toCampaignSpendings(plan, campaign))
                .forEach(campaignSpendingRepository::save);
    }
}
