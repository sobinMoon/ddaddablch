package com.donation.ddb.Controller;

import com.donation.ddb.Dto.Response.CampaignResponseDto;
import com.donation.ddb.Service.CampaignService.CampaignQueryService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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
        List<CampaignResponseDto> popular = campaignService.findAllCampaigns(
                null,
                null,
                "IN_PROGRESS",
                "POPULAR",
                pageable);
        List<CampaignResponseDto> latest = campaignService.findAllCampaigns(
                null,
                null,
                "IN_PROGRESS",
                "LATEST",
                pageable);
        List<CampaignResponseDto> endingSoon = campaignService.findAllCampaigns(
                null,
                null,
                "IN_PROGRESS",
                "ENDING_SOON",
                pageable);

        Map<String, List<CampaignResponseDto>> campaignResponseDtoList = Map.of(
                "popular", popular,
                "latest", latest,
                "endingSoon", endingSoon
        );

        return ResponseEntity.ok(campaignResponseDtoList);
    }

    // 진행 중 캠페인 리스트
    @GetMapping("in-progress")
    public ResponseEntity<?> inProgressCampaignList(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "sortType", required = false) String sortType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        List<CampaignResponseDto> campaignResponseDtoList = campaignService.findAllCampaigns(
                null,
                category,
                "IN_PROGRESS",
                sortType,
                pageable
        );

        Map<String, Object> resMap = Map.of(
                "inProgress", campaignResponseDtoList,
                "pageable", pageable,
                "totalElements", campaignResponseDtoList.size()
        );

        return ResponseEntity.ok(resMap);
    }

    // 완료된 캠페인 리스트
    @GetMapping("ended")
    public ResponseEntity<?> endedCampaignList(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "sortType", required = false) String sortType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        List<CampaignResponseDto> campaignResponseDtoList = campaignService.findAllCampaigns(
                null,
                category,
                "ENDED",
                sortType,
                pageable
        );

        Map<String, Object> resMap = Map.of(
                "inProgress", campaignResponseDtoList,
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

        List<CampaignResponseDto> campaignResponseDtoList = campaignService.findAllCampaigns(
                keyword,
                category,
                statusFlag,
                sortType,
                pageable
        );

        Map<String, Object> resMap = Map.of(
                "result", campaignResponseDtoList,
                "pageable", pageable,
                "totalElements", campaignResponseDtoList.size()
        );

        return ResponseEntity.ok(resMap);
    }
}
