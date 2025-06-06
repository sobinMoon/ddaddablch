package com.donation.ddb.Repository.CampaignRepository;

import com.donation.ddb.Converter.CampaignConverter;
import com.donation.ddb.Domain.*;
import com.donation.ddb.Domain.Enums.CampaignCategory;
import com.donation.ddb.Domain.Enums.CampaignSortType;
import com.donation.ddb.Domain.Enums.CampaignStatusFlag;
import com.donation.ddb.Dto.Request.CampaignRequestDto;
import com.donation.ddb.Dto.Response.CampaignResponse;
import com.donation.ddb.Dto.Response.OrgMyPageResponseDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.querydsl.core.types.Projections.constructor;

@Repository
@RequiredArgsConstructor
public class CampaignRepositoryImpl implements CampaignRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QCampaign qCampaign = QCampaign.campaign;

    private final EntityManager em;

    @Override
    public List<CampaignResponse.CampaignListDto> dynamicQueryWithBooleanBuilder(
            String keyword,
            CampaignCategory category,
            CampaignStatusFlag statusFlag,
            CampaignSortType sortType,
            Integer size
    ) {

        System.out.println("dynamicQueryWithBooleanBuilder called with keyword: " + keyword + ", sortType: " + sortType);
        BooleanBuilder predicate = new BooleanBuilder();

        // Add conditions based on the provided parameters
        if (keyword != null && !keyword.isEmpty()) {
            predicate.and(qCampaign.cName.containsIgnoreCase(keyword)
                    .or(qCampaign.cDescription.containsIgnoreCase(keyword)));
        }

        System.out.println("category: " + category);

        if (category != null && category != CampaignCategory.ALL) {
            predicate.and(qCampaign.cCategory.eq(category));
        }

        if (statusFlag != null) {
            predicate.and(qCampaign.cStatusFlag.eq(statusFlag));
        }

        System.out.println("Predicate after keyword check: " + predicate);

        System.out.println("jpaQueryFactory: " + jpaQueryFactory);

        JPAQuery<CampaignResponse.CampaignListDto> query = jpaQueryFactory
                .select(constructor(
                        CampaignResponse.CampaignListDto.class,
                        qCampaign.cId,
                        qCampaign.cName,
                        qCampaign.cImageUrl,
                        qCampaign.cDescription,
                        qCampaign.cGoal,
                        qCampaign.cCurrentAmount,
                        qCampaign.cCategory,
                        qCampaign.donateCount,
                        qCampaign.donateStart,
                        qCampaign.donateEnd,
                        qCampaign.businessStart,
                        qCampaign.businessEnd,
                        qCampaign.cStatusFlag,
                        qCampaign.createdAt,
                        qCampaign.updatedAt
                        )
                )
                .from(qCampaign)
                .where(predicate);

        System.out.println("Query after applying predicate: " + query);

        if (size != null && size > 0) {
            query.limit(size);
        }

        // Sort based on the provided sort type
        if (sortType != null) {
            switch (sortType) {
                case ENDING_SOON:
                    query.orderBy(qCampaign.donateEnd.asc());
                    break;
                case POPULAR:
                    query.orderBy(qCampaign.donateCount.desc());
                    break;
                case DONATION_AMOUNT:
                    query.orderBy(qCampaign.cCurrentAmount.desc());
                    break;
                default:
                    break;
            }
        }

        query.orderBy(qCampaign.cId.desc()); // Default sorting by ID in descending order

        System.out.println("Executing query with predicate: " + predicate);

        return query.fetch();
    }

    @Override
    public Campaign addCampaign(CampaignRequestDto.JoinDto request, OrganizationUser organizationUser) {

        Campaign campaign = CampaignConverter.toCampaign(request, organizationUser);

        em.persist(campaign);

        return campaign;
    }

    @Override
    public BigDecimal getTotalRaisedAmountByOrganization(Long organizationId) {
        QCampaign campaign = QCampaign.campaign;

        BigDecimal totalAmount = jpaQueryFactory
                .select(campaign.cCurrentAmount.sum().coalesce(BigDecimal.ZERO))
                .from(campaign)
                .where(campaign.organizationUser.oId.eq(organizationId))
                .fetchOne();

        return totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    @Override
    public List<OrgMyPageResponseDTO.CampaignSummaryDTO> getCampaignsByStatusAndOrganization(
            Long organizationId, List<CampaignStatusFlag> statuses) {

        QCampaign campaign = QCampaign.campaign;
        QOrganizationUser organization = QOrganizationUser.organizationUser;

        return jpaQueryFactory
                .select(Projections.constructor(
                        OrgMyPageResponseDTO.CampaignSummaryDTO.class,
                        campaign.cId,                    // Long campaignId
                        campaign.cName,                  // String campaignName
                        organization.oName,              // String organizationName
                        campaign.cDescription,           // String description
                        campaign.cCurrentAmount,         // BigDecimal currentAmount
                        campaign.cGoal,                  // Integer goalAmount
                        campaign.cImageUrl,              // String imageUrl
                        campaign.cStatusFlag,            // CampaignStatusFlag status
                        campaign.createdAt               // LocalDateTime createdAt
                ))
                .from(campaign)
                .join(campaign.organizationUser, organization)
                .where(campaign.organizationUser.oId.eq(organizationId)
                        .and(campaign.cStatusFlag.in(statuses)))
                .orderBy(campaign.createdAt.desc())
                .fetch();
    }
}
