package com.donation.ddb.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCampaign is a Querydsl query type for Campaign
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCampaign extends EntityPathBase<Campaign> {

    private static final long serialVersionUID = -1303843361L;

    public static final QCampaign campaign = new QCampaign("campaign");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final DatePath<java.time.LocalDate> businessEnd = createDate("businessEnd", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> businessStart = createDate("businessStart", java.time.LocalDate.class);

    public final ListPath<CampaignPlan, QCampaignPlan> campaignPlanList = this.<CampaignPlan, QCampaignPlan>createList("campaignPlanList", CampaignPlan.class, QCampaignPlan.class, PathInits.DIRECT2);

    public final ListPath<CampaignSpending, QCampaignSpending> campaignSpendingList = this.<CampaignSpending, QCampaignSpending>createList("campaignSpendingList", CampaignSpending.class, QCampaignSpending.class, PathInits.DIRECT2);

    public final EnumPath<CampaignCategory> cCategory = createEnum("cCategory", CampaignCategory.class);

    public final NumberPath<Integer> cCurrentAmount = createNumber("cCurrentAmount", Integer.class);

    public final StringPath cDescription = createString("cDescription");

    public final NumberPath<Integer> cGoal = createNumber("cGoal", Integer.class);

    public final NumberPath<Long> cId = createNumber("cId", Long.class);

    public final StringPath cImageUrl = createString("cImageUrl");

    public final StringPath cName = createString("cName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final EnumPath<CampaignStatusFlag> cStatusFlag = createEnum("cStatusFlag", CampaignStatusFlag.class);

    public final NumberPath<Long> donateCount = createNumber("donateCount", Long.class);

    public final DatePath<java.time.LocalDate> donateEnd = createDate("donateEnd", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> donateStart = createDate("donateStart", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QCampaign(String variable) {
        super(Campaign.class, forVariable(variable));
    }

    public QCampaign(Path<? extends Campaign> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCampaign(PathMetadata metadata) {
        super(Campaign.class, metadata);
    }

}

