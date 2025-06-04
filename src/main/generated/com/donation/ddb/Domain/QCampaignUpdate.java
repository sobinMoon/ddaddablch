package com.donation.ddb.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCampaignUpdate is a Querydsl query type for CampaignUpdate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCampaignUpdate extends EntityPathBase<CampaignUpdate> {

    private static final long serialVersionUID = -1391071352L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCampaignUpdate campaignUpdate = new QCampaignUpdate("campaignUpdate");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QCampaign campaign;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath cuContent = createString("cuContent");

    public final NumberPath<Long> cuId = createNumber("cuId", Long.class);

    public final StringPath cuImageUrl = createString("cuImageUrl");

    public final StringPath cuTitle = createString("cuTitle");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QCampaignUpdate(String variable) {
        this(CampaignUpdate.class, forVariable(variable), INITS);
    }

    public QCampaignUpdate(Path<? extends CampaignUpdate> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCampaignUpdate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCampaignUpdate(PathMetadata metadata, PathInits inits) {
        this(CampaignUpdate.class, metadata, inits);
    }

    public QCampaignUpdate(Class<? extends CampaignUpdate> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.campaign = inits.isInitialized("campaign") ? new QCampaign(forProperty("campaign"), inits.get("campaign")) : null;
    }

}

