package com.donation.ddb.Service.CampaignService;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.CampaignCategory;
import com.donation.ddb.Domain.CampaignSortType;
import com.donation.ddb.Domain.CampaignStatusFlag;
import com.donation.ddb.Dto.Response.CampaignResponse;
import com.donation.ddb.Repository.CampaignRepository.CampaignRepository;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.handler.CampaignHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignQueryServiceImpl implements CampaignQueryService {

    private final CampaignRepository campaignRepository;

    @Override
    public List<CampaignResponse.CampaignListDto> findAllCampaigns(String keyword, String category, String statusFlag, String sortType, Pageable pageable) {
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

        CampaignStatusFlag status;
        if (statusFlag == null) {
            status = CampaignStatusFlag.IN_PROGRESS;
        } else {
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
        List<CampaignResponse.CampaignListDto> campaigns = campaignRepository.dynamicQueryWithBooleanBuilder(keyword, campaignCategory, status, sort, pageable);

        campaigns.forEach(campaign -> {
            System.out.println("Campaign " + campaign);
        });

        return campaigns;
    }

    @Override
    public Campaign addCampaign(
            Long oId,
            String cName,
            String cImageUrl,
            String cDescription,
            Integer cGoal,
            CampaignCategory cCategory,
            LocalDate donateStart,
            LocalDate donateEnd,
            LocalDate businessStart,
            LocalDate businessEnd
    ) {

        System.out.println(
                "oId: " + oId +
                ", cName: " + cName +
                ", CImageUrl: " + cImageUrl +
                ", cDescription: " + cDescription +
                ", cGoal: " + cGoal +
                ", cCategory: " + cCategory +
                ", donateStart: " + donateStart +
                ", donateEnd: " + donateEnd +
                ", businessStart: " + businessStart +
                ", businessEnd: " + businessEnd
        );

        Campaign campaign = campaignRepository.addCampaign(
                oId,
                cName,
                cImageUrl,
                cDescription,
                cGoal,
                cCategory,
                donateStart,
                donateEnd,
                businessStart,
                businessEnd
        );

        return campaign;
    }

    @Override
    public Campaign findBycId(Long cId) {
        return campaignRepository.findBycId(cId);
    }
}
