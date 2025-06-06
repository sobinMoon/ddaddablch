package com.donation.ddb.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostComment is a Querydsl query type for PostComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostComment extends EntityPathBase<PostComment> {

    private static final long serialVersionUID = 288466544L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostComment postComment = new QPostComment("postComment");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath pcContent = createString("pcContent");

    public final NumberPath<Long> pcId = createNumber("pcId", Long.class);

    public final QPost post;

    public final ListPath<PostCommentLike, QPostCommentLike> postCommentLikeList = this.<PostCommentLike, QPostCommentLike>createList("postCommentLikeList", PostCommentLike.class, QPostCommentLike.class, PathInits.DIRECT2);

    public final QStudentUser studentUser;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPostComment(String variable) {
        this(PostComment.class, forVariable(variable), INITS);
    }

    public QPostComment(Path<? extends PostComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostComment(PathMetadata metadata, PathInits inits) {
        this(PostComment.class, metadata, inits);
    }

    public QPostComment(Class<? extends PostComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
        this.studentUser = inits.isInitialized("studentUser") ? new QStudentUser(forProperty("studentUser")) : null;
    }

}

