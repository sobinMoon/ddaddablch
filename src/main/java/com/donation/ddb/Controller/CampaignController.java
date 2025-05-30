package com.donation.ddb.Controller;

import com.donation.ddb.Converter.CampaignCommentLikeConverter;
import com.donation.ddb.Domain.*;
import com.donation.ddb.Dto.Request.CampaignCommentRequestDto;
import com.donation.ddb.Dto.Request.CampaignRequestDto;
import com.donation.ddb.Dto.Response.CampaignResponse;
import com.donation.ddb.Dto.Response.OrganizationResponse;
import com.donation.ddb.Service.CampaignCommentLikeService.CampaignCommentLikeService;
import com.donation.ddb.Service.CampaignCommentQueryService.CampaignCommentQueryService;
import com.donation.ddb.Service.CampaignPlansQueryService.CampaignPlansQueryService;
import com.donation.ddb.Service.CampaignService.CampaignQueryService;
import com.donation.ddb.Service.CampaignSpendingQueryService.CampaignSpendingQueryService;
import com.donation.ddb.Service.OrganizationUserService.OrganizationUserQueryService;
import com.donation.ddb.apiPayload.ApiResponse;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.handler.CampaignHandler;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    private CampaignCommentQueryService campaignCommentQueryService;

    @Autowired
    private CampaignCommentQueryService campaignCommentService;

    @Autowired
    private CampaignCommentLikeService campaignCommentLikeService;;

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
            @RequestBody @Valid CampaignRequestDto.JoinDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // 캠페인 추가 로직
        String email = null;

        if (userDetails == null) {
            throw new CampaignHandler(ErrorStatus._UNAUTHORIZED);
        }
        else if (!userDetails.isOrganization()) {
            throw new CampaignHandler(ErrorStatus._FORBIDDEN);
        }
        else {
            email = userDetails.getUsername();
        }

        Campaign campaign = campaignService.addCampaign(request, email);

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

    @GetMapping("{cId}/comments")
    public ApiResponse<?> getCampaignComments(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(value = "cId") Long cId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        log.info("userDetails: {}", userDetails);
        log.info("userDetails.getUsername(): {}", userDetails != null ? userDetails.getUsername() : "null");
        log.info("isStudent: {}", userDetails != null && userDetails.isStudent());
        log.info("getAuthorities(): {}", userDetails != null ? userDetails.getAuthorities() : "null");
        log.info("role: {}", userDetails != null ? userDetails.getAuthorities().stream().findFirst().orElse(null) : "null");

        String userEmail = null;

        if (userDetails != null && userDetails.isStudent()) {
            userEmail = userDetails.getUsername();
            System.out.println("User Email: " + userEmail);
        }

        if (page < 0) {
            throw new CampaignHandler(ErrorStatus.PAGE_NUMBER_INVALID);
        }
        if (size <= 0) {
            throw new CampaignHandler(ErrorStatus.PAGE_SIZE_INVALID);
        }

        Pageable pageable = PageRequest.of(page, size);

        return ApiResponse.onSuccess(campaignCommentQueryService.findCommentByCampaignId(cId, pageable, userEmail));
    }

    @PostMapping("{cId}/comments")
    public ApiResponse<?> addCampaignComment(
            @PathVariable(value = "cId") Long cId,
            @RequestBody CampaignCommentRequestDto campaignCommentRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // 캠페인 댓글 추가 로직
        if (userDetails == null) {
            throw new CampaignHandler(ErrorStatus._UNAUTHORIZED);
        }

        if (!userDetails.isStudent()) {
            throw new CampaignHandler(ErrorStatus._FORBIDDEN);
        }

        log.info("userDetails: {}", userDetails);
        log.info("email: {}", userDetails.getUsername());

        String email = userDetails.getUsername();

        CampaignComment newComment = campaignCommentService.addComment(
                campaignCommentRequestDto.getContent(),
                cId,
                email
        );

        return ApiResponse.onSuccess(
                Map.of(
                        "commentId", newComment.getCcId(),
                        "content", newComment.getCcContent(),
                        "createdAt", newComment.getCreatedAt()
                )
        );
    }

    @PostMapping("{cId}/comments/{ccId}/likes")
    public ApiResponse<?> addCommentLike(
            @PathVariable(value = "ccId") Long ccId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new CampaignHandler(ErrorStatus._UNAUTHORIZED);
        }

        if (!userDetails.isStudent()) {
            throw new CampaignHandler(ErrorStatus._FORBIDDEN);
        }

        String email = userDetails.getUsername();

        CampaignCommentLike campaignCommentLike = campaignCommentLikeService.toggleCommentLike(ccId, email);

        return ApiResponse.onSuccess(CampaignCommentLikeConverter.toDto(campaignCommentLike));

    }

    public CampaignResponse.CampaignListDto convertToListDto(Campaign campaign) {
        return CampaignResponse.CampaignListDto.builder()
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
                .statusFlag(campaign.getCStatusFlag())
                .createdAt(campaign.getCreatedAt())
                .updatedAt(campaign.getUpdatedAt())
                .build();
    }

    public CampaignResponse.CampaignDetailDto convertToDetailDto(Campaign campaign) {
        // 변경된 부분: 올바른 타입 사용
        OrganizationResponse.OrganizationDetailDto organizationDto = null;

        // null 체크 유지
        if (campaign.getOrganizationUser() != null) {
            organizationDto = organizationUserQueryService.convertToDetailDto(campaign.getOrganizationUser());
        }

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
                .statusFlag(campaign.getCStatusFlag())
                .walletAddress(campaign.getCWalletAddress())
                .createdAt(campaign.getCreatedAt())
                .updatedAt(campaign.getUpdatedAt())
                .organization(organizationDto)
                .campaignPlans(campaignPlansQueryService.getCampaignPlanDetails(campaign.getCId()))
                .campaignSpendings(campaignSpendingQueryService.getCampaignSpending(campaign.getCId()))
                .build();
    }
}
