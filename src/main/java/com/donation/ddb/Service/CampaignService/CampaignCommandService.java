package com.donation.ddb.Service.CampaignService;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.Enums.CampaignStatusFlag;
import com.donation.ddb.Domain.OrganizationUser;
import com.donation.ddb.Dto.Request.CampaignRequestDto;
import com.donation.ddb.Repository.CampaignRepository.CampaignRepository;
import com.donation.ddb.Repository.OrganizationUserRepository;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.handler.CampaignHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CampaignCommandService {
    private final CampaignRepository campaignRepository;
    private final OrganizationUserRepository organizationUserRepository;

    public Campaign addCampaign(CampaignRequestDto.JoinDto joinDto, String email) {

        OrganizationUser user = organizationUserRepository.findByoEmail(email).
                orElseThrow(() -> new CampaignHandler(ErrorStatus.ORGANIZATION_USER_NOT_FOUND));

        return campaignRepository.addCampaign(joinDto, user);
    }

    public Campaign updateCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    public void updateStatusByDate() {
        List<Campaign> campaigns = campaignRepository.findAll();

        LocalDate today = LocalDate.now();
        for (Campaign campaign : campaigns) {

            CampaignStatusFlag currentStatus = campaign.getCStatusFlag();
            CampaignStatusFlag newStatus = CampaignStatusFlag.FUNDRAISING;

            if (!campaign.getBusinessEnd().isAfter(today)) {
                newStatus = CampaignStatusFlag.COMPLETED;
            } else if (!campaign.getBusinessStart().isAfter(today)) {
                newStatus = CampaignStatusFlag.IN_PROGRESS;
            } else if (!campaign.getDonateEnd().isAfter(today)) {
                newStatus = CampaignStatusFlag.FUNDED;
            }

            if (CampaignStatusFlag.isForwardTransition(currentStatus, newStatus)) {
                campaign.setCStatusFlag(newStatus);
            }
        }
    }

    public Campaign updateStatusByUser(Campaign campaign, CampaignStatusFlag statusFlag) {

        if (!CampaignStatusFlag.isForwardTransition(campaign.getCStatusFlag(), statusFlag)) {
            throw new CampaignHandler(ErrorStatus.CAMPAIGN_INVALID_STATUS_UPDATE);
        }

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
