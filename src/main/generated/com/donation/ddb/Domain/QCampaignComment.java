package com.donation.ddb.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCampaignComment is a Querydsl query type for CampaignComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCampaignComment extends EntityPathBase<CampaignComment> {

    private static final long serialVersionUID = 1011289984L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCampaignComment campaignComment = new QCampaignComment("campaignComment");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QCampaign campaign;

    public final ListPath<CampaignCommentLike, QCampaignCommentLike> campaignCommentLikeList = this.<CampaignCommentLike, QCampaignCommentLike>createList("campaignCommentLikeList", CampaignCommentLike.class, QCampaignCommentLike.class, PathInits.DIRECT2);

    public final StringPath ccContent = createString("ccContent");

    public final NumberPath<Long> ccId = createNumber("ccId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final QStudentUser studentUser;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QCampaignComment(String variable) {
        this(CampaignComment.class, forVariable(variable), INITS);
    }

    public QCampaignComment(Path<? extends CampaignComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCampaignComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCampaignComment(PathMetadata metadata, PathInits inits) {
        this(CampaignComment.class, metadata, inits);
    }

    public QCampaignComment(Class<? extends CampaignComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.campaign = inits.isInitialized("campaign") ? new QCampaign(forProperty("campaign"), inits.get("campaign")) : null;
        this.studentUser = inits.isInitialized("studentUser") ? new QStudentUser(forProperty("studentUser")) : null;
    }

}

