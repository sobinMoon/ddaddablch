package com.donation.ddb.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrganizationUser is a Querydsl query type for OrganizationUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrganizationUser extends EntityPathBase<OrganizationUser> {

    private static final long serialVersionUID = 944187757L;

    public static final QOrganizationUser organizationUser = new QOrganizationUser("organizationUser");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final ListPath<Campaign, QCampaign> campaigns = this.<Campaign, QCampaign>createList("campaigns", Campaign.class, QCampaign.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath oBusinessNumber = createString("oBusinessNumber");

    public final StringPath oDescription = createString("oDescription");

    public final StringPath oEmail = createString("oEmail");

    public final NumberPath<Long> oId = createNumber("oId", Long.class);

    public final BooleanPath oIsActive = createBoolean("oIsActive");

    public final StringPath oName = createString("oName");

    public final StringPath oPassword = createString("oPassword");

    public final StringPath oProfileImage = createString("oProfileImage");

    public final EnumPath<WalletAuthStatus> oWalletAuthStatus = createEnum("oWalletAuthStatus", WalletAuthStatus.class);

    public final EnumPath<Role> role = createEnum("role", Role.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath walletAddresses = createString("walletAddresses");

    public QOrganizationUser(String variable) {
        super(OrganizationUser.class, forVariable(variable));
    }

    public QOrganizationUser(Path<? extends OrganizationUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOrganizationUser(PathMetadata metadata) {
        super(OrganizationUser.class, metadata);
    }

}

