package com.donation.ddb.Repository.CampaignRepository;


import com.donation.ddb.Domain.*;
import com.donation.ddb.Domain.Enums.CampaignCategory;
import com.donation.ddb.Domain.Enums.CampaignSortType;
import com.donation.ddb.Domain.Enums.CampaignStatusFlag;
import com.donation.ddb.Dto.Request.CampaignRequestDto;
import com.donation.ddb.Dto.Response.CampaignResponse;
import com.donation.ddb.Dto.Response.OrgMyPageResponseDTO;
import com.donation.ddb.apiPayload.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CampaignRepositoryCustom {

    List<CampaignResponse.CampaignListDto> dynamicQueryWithBooleanBuilder(
            String keyword,
            CampaignCategory category,
            CampaignStatusFlag statusFlag,
            CampaignSortType sortType,
            Integer size
    );

    // 조직의 총 모금액 조회
    BigDecimal getTotalRaisedAmountByOrganization(Long organizationId);

    // 상태별 캠페인 조회
    List<OrgMyPageResponseDTO.CampaignSummaryDTO> getCampaignsByStatusAndOrganization(
            Long organizationId, List<CampaignStatusFlag> statuses);

    Campaign addCampaign(CampaignRequestDto.JoinDto joinDto, OrganizationUser organizationUser);
}

