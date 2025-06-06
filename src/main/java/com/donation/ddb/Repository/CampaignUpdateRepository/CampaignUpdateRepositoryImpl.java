package com.donation.ddb.Repository.CampaignUpdateRepository;

import com.donation.ddb.Domain.CampaignUpdate;
import com.donation.ddb.Domain.QCampaign;
import com.donation.ddb.Domain.QCampaignUpdate;
import com.donation.ddb.Repository.projection.CampaignWithUpdate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CampaignUpdateRepositoryImpl implements CampaignUpdateRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QCampaignUpdate campaignUpdate = QCampaignUpdate.campaignUpdate;
    private final QCampaign campaign = QCampaign.campaign;

    @Override
    public List<CampaignWithUpdate> findLatestUpdates() {
        List<CampaignWithUpdate> result = queryFactory
                .select(Projections.constructor(
                        CampaignWithUpdate.class,
                        campaign.cId,
                        campaignUpdate.cuTitle,
                        campaignUpdate.cuContent,
                        campaignUpdate.createdAt
                ))
                .from(campaignUpdate)
                .join(campaignUpdate.campaign, campaign)
                .orderBy(campaignUpdate.createdAt.desc())
                .limit(3)
                .fetch();

        return result;
    }
}
