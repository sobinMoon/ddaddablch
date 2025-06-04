package com.donation.ddb.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCampaignCommentLike is a Querydsl query type for CampaignCommentLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCampaignCommentLike extends EntityPathBase<CampaignCommentLike> {

    private static final long serialVersionUID = -1688767689L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCampaignCommentLike campaignCommentLike = new QCampaignCommentLike("campaignCommentLike");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QCampaignComment campaignComment;

    public final NumberPath<Long> cclId = createNumber("cclId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final QStudentUser studentUser;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QCampaignCommentLike(String variable) {
        this(CampaignCommentLike.class, forVariable(variable), INITS);
    }

    public QCampaignCommentLike(Path<? extends CampaignCommentLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCampaignCommentLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCampaignCommentLike(PathMetadata metadata, PathInits inits) {
        this(CampaignCommentLike.class, metadata, inits);
    }

    public QCampaignCommentLike(Class<? extends CampaignCommentLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.campaignComment = inits.isInitialized("campaignComment") ? new QCampaignComment(forProperty("campaignComment"), inits.get("campaignComment")) : null;
        this.studentUser = inits.isInitialized("studentUser") ? new QStudentUser(forProperty("studentUser")) : null;
    }

}

