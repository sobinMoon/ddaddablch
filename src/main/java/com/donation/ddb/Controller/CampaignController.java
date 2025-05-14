package com.donation.ddb.Controller;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Dto.Request.CampaignRequestDto;
import com.donation.ddb.Dto.Response.CampaignResponse;
import com.donation.ddb.Service.CampaignPlansQueryService.CampaignPlansQueryService;
import com.donation.ddb.Service.CampaignService.CampaignQueryService;
import com.donation.ddb.Service.CampaignSpendingQueryService.CampaignSpendingQueryService;
import com.donation.ddb.Service.OrganizationUserService.OrganizationUserQueryService;
import com.donation.ddb.apiPayload.ApiResponse;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.handler.CampaignHandler;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private OrganizationUserQueryService organizationUserQueryService;

    @Autowired
    private CampaignPlansQueryService campaignPlansQueryService;

    @Autowired
    private CampaignSpendingQueryService campaignSpendingQueryService;

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

    @GetMapping("{cId}")
    public ApiResponse<?> getCampaign(@PathVariable(value = "cId") Long cId) {
        Campaign campaign = campaignService.findBycId(cId);
        if (campaign == null) {
            throw new CampaignHandler(ErrorStatus.CAMPAIGN_NOT_FOUND);
        }
        return ApiResponse.onSuccess(convertToDetailDto(campaign));
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

    public CampaignResponse.CampaignDetailDto convertToDetailDto(Campaign campaign) {
        return CampaignResponse.CampaignDetailDto.builder()
                .id(campaign.getCId())
                .name(campaign.getCName())
                .imageUrl(campaign.getCImageUrl())
                .description(campaign.getCDescription())
                .goal(campaign.getCGoal())
                .currentAmount(campaign.getCCurrentAmount())
                .category(campaign.getCCategory())
                .donateCount(campaign.getDonateCount())
                .donateStart(campaign.getDonateStart())
                .donateEnd(campaign.getDonateEnd())
                .businessStart(campaign.getBusinessStart())
                .businessEnd(campaign.getBusinessEnd())
                .cStatusFlag(campaign.getCStatusFlag())
                .createdAt(campaign.getCreatedAt())
                .updatedAt(campaign.getUpdatedAt())
                .organization(organizationUserQueryService.convertToDetailDto(campaign.getOrganizationUser()))
                .campaignPlans(campaignPlansQueryService.getCampaignPlanDetails(campaign.getCId()))
                .campaignSpendings(campaignSpendingQueryService.getCampaignSpending(campaign.getCId()))
                .build();
    }
}
