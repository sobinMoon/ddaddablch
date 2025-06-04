package com.donation.ddb.Service.CampaignService;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.CampaignStatusFlag;
import com.donation.ddb.Repository.CampaignRepository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignCommandService {
    private final CampaignRepository campaignRepository;

    @Transactional
    public void updateStatusByDate() {
        List<Campaign> campaigns = campaignRepository.findAll();

        LocalDate today = LocalDate.now();
        for (Campaign campaign : campaigns) {
            if (!campaign.getBusinessEnd().isAfter(today)) {
                campaign.setCStatusFlag(CampaignStatusFlag.COMPLETED);
            } else if (!campaign.getBusinessStart().isAfter(today)) {
                campaign.setCStatusFlag(CampaignStatusFlag.IN_PROGRESS);
            } else if (!campaign.getDonateEnd().isAfter(today)) {
                campaign.setCStatusFlag(CampaignStatusFlag.FUNDED);
            }
        }
    }

    @Transactional
    public Campaign updateStatusByUser(Campaign campaign, CampaignStatusFlag statusFlag) {
        campaign.setCStatusFlag(statusFlag);
        if (statusFlag == CampaignStatusFlag.FUNDED) {
            campaign.setDonateEnd(LocalDate.now());
        } else if (statusFlag == CampaignStatusFlag.IN_PROGRESS) {
            campaign.setBusinessStart(LocalDate.now());
        } else if (statusFlag == CampaignStatusFlag.COMPLETED) {
            campaign.setBusinessEnd(LocalDate.now());
        }
        return campaignRepository.save(campaign);
    }
}
