package com.donation.ddb.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostCommentLike is a Querydsl query type for PostCommentLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostCommentLike extends EntityPathBase<PostCommentLike> {

    private static final long serialVersionUID = 977080871L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostCommentLike postCommentLike = new QPostCommentLike("postCommentLike");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> pclId = createNumber("pclId", Long.class);

    public final QPostComment postComment;

    public final QStudentUser studentUser;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPostCommentLike(String variable) {
        this(PostCommentLike.class, forVariable(variable), INITS);
    }

    public QPostCommentLike(Path<? extends PostCommentLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostCommentLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostCommentLike(PathMetadata metadata, PathInits inits) {
        this(PostCommentLike.class, metadata, inits);
    }

    public QPostCommentLike(Class<? extends PostCommentLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.postComment = inits.isInitialized("postComment") ? new QPostComment(forProperty("postComment"), inits.get("postComment")) : null;
        this.studentUser = inits.isInitialized("studentUser") ? new QStudentUser(forProperty("studentUser")) : null;
    }

}

