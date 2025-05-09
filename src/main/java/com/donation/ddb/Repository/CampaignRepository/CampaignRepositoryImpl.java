package com.donation.ddb.Repository.CampaignRepository;

import com.donation.ddb.Domain.CampaignCategory;
import com.donation.ddb.Domain.CampaignSortType;
import com.donation.ddb.Domain.CampaignStatusFlag;
import com.donation.ddb.Domain.QCampaign;
import com.donation.ddb.Dto.Response.CampaignResponseDto;
import com.donation.ddb.apiPayload.ApiResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.util.List;

import static com.querydsl.core.types.Projections.constructor;

@Repository
@RequiredArgsConstructor
public class CampaignRepositoryImpl implements CampaignRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QCampaign qCampaign = QCampaign.campaign;


    @Override
    public List<CampaignResponseDto> dynamicQueryWithBooleanBuilder(
            String keyword,
            CampaignCategory category,
            CampaignStatusFlag statusFlag,
            CampaignSortType sortType,
            Pageable pageable
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

        JPAQuery<CampaignResponseDto> query = jpaQueryFactory
                .select(constructor(
                        CampaignResponseDto.class,
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

        // Apply pagination
        if (pageable != null) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        return query.fetch();
    }

    @Override
    public CampaignResponseDto addCampaign(Long oId, String cName, String CImageUrl, String cDescription, Integer cGoal, CampaignCategory cCategory, LocalDate donateStart, LocalDate donateEnd, LocalDate businessStart, LocalDate businessEnd) {



        return null;
    }
}
