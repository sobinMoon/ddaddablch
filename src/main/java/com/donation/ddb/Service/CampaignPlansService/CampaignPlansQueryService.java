package com.donation.ddb.Service.CampaignPlansService;

import com.donation.ddb.Domain.CampaignPlan;
import com.donation.ddb.Dto.Response.CampaignPlanResponseDto;
import com.donation.ddb.Repository.CampaignPlansRepository.CampaignPlansRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignPlansQueryService {
    private final CampaignPlansRepository campaignPlansRepository;

    public List<CampaignPlanResponseDto> getCampaignPlanDetails(Long cId) {
        List<CampaignPlan> campaignPlans = campaignPlansRepository.findByCampaign_cId(cId);

        List<CampaignPlanResponseDto> campaignPlanResponseList = campaignPlans.stream()
                .map(
                        campaignPlan -> CampaignPlanResponseDto.builder()
                                .id(campaignPlan.getCpId())
                                .title(campaignPlan.getCpTitle())
                                .amount(campaignPlan.getCpAmount())
                                .build()
                )
                .toList();

        return campaignPlanResponseList;
    }
}
