package com.donation.ddb.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCampaignPlan is a Querydsl query type for CampaignPlan
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCampaignPlan extends EntityPathBase<CampaignPlan> {

    private static final long serialVersionUID = 1719068072L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCampaignPlan campaignPlan = new QCampaignPlan("campaignPlan");

    public final QCampaign campaign;

    public final NumberPath<Integer> cpAmount = createNumber("cpAmount", Integer.class);

    public final NumberPath<Long> cpId = createNumber("cpId", Long.class);

    public final StringPath cpTitle = createString("cpTitle");

    public QCampaignPlan(String variable) {
        this(CampaignPlan.class, forVariable(variable), INITS);
    }

    public QCampaignPlan(Path<? extends CampaignPlan> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCampaignPlan(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCampaignPlan(PathMetadata metadata, PathInits inits) {
        this(CampaignPlan.class, metadata, inits);
    }

    public QCampaignPlan(Class<? extends CampaignPlan> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.campaign = inits.isInitialized("campaign") ? new QCampaign(forProperty("campaign"), inits.get("campaign")) : null;
    }

}

