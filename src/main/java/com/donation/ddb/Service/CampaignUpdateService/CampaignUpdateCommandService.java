package com.donation.ddb.Service.CampaignUpdateService;

import com.donation.ddb.Converter.CampaignUpdateConverter;
import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.CampaignUpdate;
import com.donation.ddb.Dto.Request.CampaignUpdateRequestDto;
import com.donation.ddb.Repository.CampaignUpdateRepository.CampaignUpdateRepository;
import com.donation.ddb.Service.CampaignService.CampaignQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignUpdateCommandService {
    private final CampaignUpdateRepository campaignUpdateRepository;
    private final CampaignQueryService campaignQueryService;

    public CampaignUpdate addCampaignUpdate(CampaignUpdateRequestDto.JoinDto request, String imagePath, Long campaignId) {

        Campaign campaign = campaignQueryService.findBycId(campaignId);

        return campaignUpdateRepository.save(CampaignUpdateConverter.toCampaignUpdate(request, imagePath, campaign));
    }
}
