package com.donation.ddb.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCampaignSpending is a Querydsl query type for CampaignSpending
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCampaignSpending extends EntityPathBase<CampaignSpending> {

    private static final long serialVersionUID = -235227965L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCampaignSpending campaignSpending = new QCampaignSpending("campaignSpending");

    public final QCampaign campaign;

    public final NumberPath<Integer> csAmount = createNumber("csAmount", Integer.class);

    public final NumberPath<Long> csId = createNumber("csId", Long.class);

    public final StringPath csTitle = createString("csTitle");

    public QCampaignSpending(String variable) {
        this(CampaignSpending.class, forVariable(variable), INITS);
    }

    public QCampaignSpending(Path<? extends CampaignSpending> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCampaignSpending(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCampaignSpending(PathMetadata metadata, PathInits inits) {
        this(CampaignSpending.class, metadata, inits);
    }

    public QCampaignSpending(Class<? extends CampaignSpending> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.campaign = inits.isInitialized("campaign") ? new QCampaign(forProperty("campaign"), inits.get("campaign")) : null;
    }

}

