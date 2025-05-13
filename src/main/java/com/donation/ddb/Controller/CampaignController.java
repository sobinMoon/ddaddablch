package com.donation.ddb.Controller;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Dto.Request.CampaignRequestDto;
import com.donation.ddb.Dto.Response.CampaignResponse;
import com.donation.ddb.Service.CampaignService.CampaignQueryService;
import com.donation.ddb.apiPayload.ApiResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.querydsl.core.types.Projections.constructor;

@RestController
@NoArgsConstructor
@RequestMapping("/api/v1/campaigns")
@Slf4j
public class CampaignController {

    @Autowired
    private CampaignQueryService campaignService;

    @GetMapping("home")
    public ResponseEntity<?> campaignList() {
        Pageable pageable = PageRequest.of(0, 3);
        List<CampaignResponse.CampaignListDto> popular = campaignService.findAllCampaigns(
                null,
                null,
                "FUNDRAISING",
                "POPULAR",
                pageable);
        List<CampaignResponse.CampaignListDto> latest = campaignService.findAllCampaigns(
                null,
                null,
                "FUNDRAISING",
                "LATEST",
                pageable);
        List<CampaignResponse.CampaignListDto> endingSoon = campaignService.findAllCampaigns(
                null,
                null,
                "FUNDRAISING",
                "ENDING_SOON",
                pageable);

        Map<String, List<CampaignResponse.CampaignListDto>> campaignResponseDtoList = Map.of(
                "popular", popular,
                "latest", latest,
                "endingSoon", endingSoon
        );

        return ResponseEntity.ok(campaignResponseDtoList);
    }

    // 진행 중 캠페인 리스트
    @GetMapping("fundraising")
    public ResponseEntity<?> inProgressCampaignList(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "sortType", required = false) String sortType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        List<CampaignResponse.CampaignListDto> campaignResponseDtoList = campaignService.findAllCampaigns(
                null,
                category,
                "FUNDRAISING",
                sortType,
                pageable
        );

        Map<String, Object> resMap = Map.of(
                "campaigns", campaignResponseDtoList,
                "pageable", pageable,
                "totalElements", campaignResponseDtoList.size()
        );

        return ResponseEntity.ok(resMap);
    }

    // 완료된 캠페인 리스트
    @GetMapping("completed")
    public ResponseEntity<?> endedCampaignList(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "sortType", required = false) String sortType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        List<CampaignResponse.CampaignListDto> campaignResponseDtoList = campaignService.findAllCampaigns(
                null,
                category,
                "COMPLETED",
                sortType,
                pageable
        );

        Map<String, Object> resMap = Map.of(
                "campaigns", campaignResponseDtoList,
                "pageable", pageable,
                "totalElements", campaignResponseDtoList.size()
        );

        return ResponseEntity.ok(resMap);
    }

    // 검색
    @GetMapping("search")
    public ResponseEntity<?> campaignList(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "statusFlag", required = false) String statusFlag,
            @RequestParam(value = "sortType", required = false) String sortType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        // CampaignCategory로 변환
        if (category == null || category.isEmpty()) {
            category = "ALL"; // 기본값 설정
        }

        // CampaignSortType으로 변환

        List<CampaignResponse.CampaignListDto> campaignResponseDtoList = campaignService.findAllCampaigns(
                keyword,
                category,
                statusFlag,
                sortType,
                pageable
        );

        Map<String, Object> resMap = Map.of(
                "campaigns", campaignResponseDtoList,
                "pageable", pageable,
                "totalElements", campaignResponseDtoList.size()
        );

        return ResponseEntity.ok(resMap);
    }

    @PostMapping
    public ApiResponse<?> addCampaign(
            @RequestBody CampaignRequestDto campaignRequestDto
    ) {
        // 캠페인 추가 로직

        System.out.println("CampaignRequestDto: " + campaignRequestDto);

        Campaign campaign = campaignService.addCampaign(
                campaignRequestDto.getOId(),
                campaignRequestDto.getCName(),
                campaignRequestDto.getCImageUrl(),
                campaignRequestDto.getCDescription(),
                campaignRequestDto.getCGoal(),
                campaignRequestDto.getCCategory(),
                campaignRequestDto.getDonateStart(),
                campaignRequestDto.getDonateEnd(),
                campaignRequestDto.getBusinessStart(),
                campaignRequestDto.getBusinessEnd()
        );

        return ApiResponse.onSuccess(convertToListDto(campaign));
    }

    public CampaignResponse.CampaignListDto convertToListDto(Campaign campaign) {
        return CampaignResponse.CampaignListDto.builder()
                .cId(campaign.getCId())
                .cName(campaign.getCName())
                .cImageUrl(campaign.getCImageUrl())
                .cDescription(campaign.getCDescription())
                .cGoal(campaign.getCGoal())
                .cCurrentAmount(campaign.getCCurrentAmount())
                .cCategory(campaign.getCCategory())
                .donateCount(campaign.getDonateCount())
                .donateStart(campaign.getDonateStart())
                .donateEnd(campaign.getDonateEnd())
                .businessStart(campaign.getBusinessStart())
                .businessEnd(campaign.getBusinessEnd())
                .cStatusFlag(campaign.getCStatusFlag())
                .createdAt(campaign.getCreatedAt())
                .updatedAt(campaign.getUpdatedAt())
                .build();
    }
}
