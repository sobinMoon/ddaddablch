package com.donation.ddb.Service.CampaignSpendingService;

import com.donation.ddb.Domain.CampaignSpending;
import com.donation.ddb.Dto.Response.CampaignSpendingResponseDto;
import com.donation.ddb.Repository.CampaignSpendingRepository.CampaignSpendingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignSpendingQueryService {
    private final CampaignSpendingRepository campaignSpendingRepository;

    public List<CampaignSpendingResponseDto> getCampaignSpending(Long cId) {
        List<CampaignSpending> campaignSpendings = campaignSpendingRepository.findByCampaign_cId(cId);

        //                                CampaignSpendingResponseDto.builder()
        //                                .id(campaignSpending.getCsId())
        //                                .title(campaignSpending.getCsTitle())
        //                                .amount(campaignSpending.getCsAmount())
        //                                .build()
        List<CampaignSpendingResponseDto> campaignSpendingResponseList = campaignSpendings.stream()
                .map(
                        CampaignSpendingResponseDto::from
                )
                .toList();

        return campaignSpendingResponseList;
    }
}
