package com.donation.ddb.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotification is a Querydsl query type for Notification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotification extends EntityPathBase<Notification> {

    private static final long serialVersionUID = 2141812282L;

    public static final QNotification notification = new QNotification("notification");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final BooleanPath isRead = createBoolean("isRead");

    public final NumberPath<Long> notificationId = createNumber("notificationId", Long.class);

    public final EnumPath<com.donation.ddb.Domain.Enums.NotificationType> notificationType = createEnum("notificationType", com.donation.ddb.Domain.Enums.NotificationType.class);

    public final NumberPath<Long> redirectUrl = createNumber("redirectUrl", Long.class);

    public final NumberPath<Long> relatedDonationId = createNumber("relatedDonationId", Long.class);

    public final NumberPath<Long> relatedPostId = createNumber("relatedPostId", Long.class);

    public final NumberPath<Long> studentId = createNumber("studentId", Long.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QNotification(String variable) {
        super(Notification.class, forVariable(variable));
    }

    public QNotification(Path<? extends Notification> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotification(PathMetadata metadata) {
        super(Notification.class, metadata);
    }

}

