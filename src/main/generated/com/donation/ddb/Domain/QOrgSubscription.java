package com.donation.ddb.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrgSubscription is a Querydsl query type for OrgSubscription
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrgSubscription extends EntityPathBase<OrgSubscription> {

    private static final long serialVersionUID = 414319218L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrgSubscription orgSubscription = new QOrgSubscription("orgSubscription");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final BooleanPath isActive = createBoolean("isActive");

    public final BooleanPath notificationEnabled = createBoolean("notificationEnabled");

    public final QOrganizationUser organizationUser;

    public final NumberPath<Long> osId = createNumber("osId", Long.class);

    public final QStudentUser studentUser;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QOrgSubscription(String variable) {
        this(OrgSubscription.class, forVariable(variable), INITS);
    }

    public QOrgSubscription(Path<? extends OrgSubscription> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrgSubscription(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrgSubscription(PathMetadata metadata, PathInits inits) {
        this(OrgSubscription.class, metadata, inits);
    }

    public QOrgSubscription(Class<? extends OrgSubscription> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.organizationUser = inits.isInitialized("organizationUser") ? new QOrganizationUser(forProperty("organizationUser")) : null;
        this.studentUser = inits.isInitialized("studentUser") ? new QStudentUser(forProperty("studentUser")) : null;
    }

}

