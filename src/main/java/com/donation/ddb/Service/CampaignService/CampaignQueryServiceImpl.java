package com.donation.ddb.Service.CampaignService;

import com.donation.ddb.Domain.*;
import com.donation.ddb.Domain.Enums.CampaignCategory;
import com.donation.ddb.Domain.Enums.CampaignSortType;
import com.donation.ddb.Domain.Enums.CampaignStatusFlag;
import com.donation.ddb.Dto.Response.CampaignResponse;
import com.donation.ddb.Repository.CampaignRepository.CampaignRepository;
import com.donation.ddb.Repository.CampaignUpdateRepository.CampaignUpdateRepository;
import com.donation.ddb.Repository.OrganizationUserRepository;
import com.donation.ddb.Repository.projection.CampaignWithUpdate;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.handler.CampaignHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignQueryServiceImpl implements CampaignQueryService {

    private final CampaignRepository campaignRepository;

    private final OrganizationUserRepository organizationUserRepository;
    private final CampaignUpdateRepository campaignUpdateRepository;

    @Override
    public List<CampaignResponse.CampaignListDto> findAllCampaigns(
            String keyword, String category, String statusFlag, String sortType, Integer size
    ) {
        CampaignCategory campaignCategory;
        if (category == null) {
            campaignCategory = CampaignCategory.ALL;
        } else {
            try {
                campaignCategory = CampaignCategory.valueOf(category);
            } catch (IllegalArgumentException e) {
                throw new CampaignHandler(ErrorStatus.CAMPAIGN_INVALID_CATEGORY);
            }
        }

        CampaignStatusFlag status = null;
        if (statusFlag != null) {
            try {
                status = CampaignStatusFlag.valueOf(statusFlag);
            } catch (IllegalArgumentException e) {
                throw new CampaignHandler(ErrorStatus.CAMPAIGN_INVALID_STATUS_FLAG);
            }

        }

        CampaignSortType sort;
        if (sortType == null) {
            sort = CampaignSortType.LATEST;
        } else {
            try {
                sort = CampaignSortType.valueOf(sortType);
            } catch (IllegalArgumentException e) {
                throw new CampaignHandler(ErrorStatus.CAMPAIGN_INVALID_SORT_TYPE);
            }
        }
        List<CampaignResponse.CampaignListDto> campaigns = campaignRepository.dynamicQueryWithBooleanBuilder(keyword, campaignCategory, status, sort, size);

        campaigns.stream().forEach(campaign -> {
            String imageUrl = campaign.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                imageUrl = imageUrl.replace("C:\\DDADDABLCH\\", "").replace("\\", "/");
                campaign.setImageUrl(imageUrl);
            }
        });
        return campaigns;
    }

    @Override
    public Campaign findBycId(Long cId) {
        return campaignRepository.findBycId(cId);
    }

    @Override
    public Boolean existsBycId(Long cId) {
        return campaignRepository.existsBycId(cId);
    }

    @Override
    public List<CampaignWithUpdate> findRecentUpdates() {
        List<CampaignWithUpdate> campaignUpdates = campaignUpdateRepository.findLatestUpdates();
        List<CampaignWithUpdate> previewUpdates = campaignUpdates.stream().map(campaignUpdate -> {
            String previewContent = campaignUpdate.getPreviewContent();
            if (previewContent != null && previewContent.length() > 200) {
                previewContent = previewContent.substring(0, 200) + "...";
            }
            campaignUpdate.setPreviewContent(previewContent);
            return campaignUpdate;
        }).toList();
        return previewUpdates;
    }

    @Override
    public Campaign findBycName(String cName) {
        return campaignRepository.findBycName(cName);
    }
}
