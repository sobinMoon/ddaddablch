package com.donation.ddb.Controller;

import com.donation.ddb.Domain.CampaignSortType;
import com.donation.ddb.Dto.Request.CampaignSearchDto;
import com.donation.ddb.Dto.Response.CampaignResponseDto;
import com.donation.ddb.Service.CampaignService.CampaignQueryService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@NoArgsConstructor
public class CampaignController {

    @Autowired
    private CampaignQueryService campaignService;

    @GetMapping("/campaign")
    // 쿼리스트링으로 keyword, sortType, page를 받을 수 있도록 수정
    public List<CampaignResponseDto> campaignList(CampaignSearchDto searchDto) {
        Pageable pageable = PageRequest.of(searchDto.getPage(), searchDto.getSize());
        // CampaignSortType으로 변환
        campaignService.findAllCampaigns(
                searchDto.getSearchKeyword(),
                searchDto.getCategory(),
                searchDto.getSortType(),
                pageable
        );
        return List.of();
    }
}
