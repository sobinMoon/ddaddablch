package com.donation.ddb.Repository.DonationRepository;

import com.donation.ddb.Domain.DonationStatus;
import com.donation.ddb.Domain.QDonation;
import com.donation.ddb.Domain.QCampaign;
import com.donation.ddb.Dto.Response.DonationStatusDTO;
import com.donation.ddb.Dto.Response.StudentMyPageResponseDTO;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DonationRepositoryImpl implements DonationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public DonationStatusDTO getDonationStatsByStudentId(Long studentId){
        QDonation donation=QDonation.donation;

        //한 번의 쿼리로 총 금액과 횟수를 동시에 조회하기
        Tuple result=queryFactory
                .select(
                        donation.amount.sum().coalesce(BigDecimal.ZERO),
                        donation.count()
                )
                .from(donation)
                .where(donation.studentUser.sId.eq(studentId)
                        .and(donation.status.eq(DonationStatus.SUCCESS)))
                .fetchOne();


        return DonationStatusDTO.builder()
                .totalAmount(result.get(donation.amount.sum().coalesce(BigDecimal.ZERO)))
                .totalCount(result.get(donation.count()).intValue())
                .build();
    }

    @Override
    public List<StudentMyPageResponseDTO.DonationSummaryDTO>
        getRecentDonationSummary(Long studentId, int limit){
            QDonation donation=QDonation.donation;
            QCampaign campaign=QCampaign.campaign;

            return queryFactory
                    .select(Projections.constructor(
                            StudentMyPageResponseDTO.DonationSummaryDTO.class,
                            donation.dId,
                            campaign.cName,
                            donation.campaign.cId,
                            donation.amount,
                            donation.createdAt,  // BaseEntity에서 상속받은 createdAt
                            donation.transactionHash,
                            donation.status.stringValue()

                    ))
                    .from(donation)
                    .join(donation.campaign,campaign)
                    .where(donation.studentUser.sId.eq(studentId)
                            .and(donation.status.eq(DonationStatus.SUCCESS)))
                    .orderBy(donation.createdAt.desc())
                    .limit(limit)
                    .fetch();
    }

}
