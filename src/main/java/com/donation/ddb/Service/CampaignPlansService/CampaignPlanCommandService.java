package com.donation.ddb.Service.CampaignPlansService;

import com.donation.ddb.Converter.CampaignPlanConverter;
import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Dto.Request.CampaignPlanRequestDto;
import com.donation.ddb.Repository.CampaignPlansRepository.CampaignPlansRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignPlanCommandService {
    private final CampaignPlansRepository campaignPlansRepository;

    public void addCampaignPlan(List<CampaignPlanRequestDto.JoinDto> plans, Campaign campaign) {
        plans.stream().
                map(plan -> CampaignPlanConverter.toCampaignPlan(plan, campaign))
                .forEach(campaignPlansRepository::save);
    }
}
