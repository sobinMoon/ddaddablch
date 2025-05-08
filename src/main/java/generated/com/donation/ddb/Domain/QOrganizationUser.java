package com.donation.ddb.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOrganizationUser is a Querydsl query type for OrganizationUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrganizationUser extends EntityPathBase<OrganizationUser> {

    private static final long serialVersionUID = 944187757L;

    public static final QOrganizationUser organizationUser = new QOrganizationUser("organizationUser");

    public final StringPath oDescription = createString("oDescription");

    public final NumberPath<Long> oId = createNumber("oId", Long.class);

    public final BooleanPath oIsActive = createBoolean("oIsActive");

    public final StringPath oName = createString("oName");

    public final StringPath oPassword = createString("oPassword");

    public final StringPath oWalletAddress = createString("oWalletAddress");

    public final EnumPath<WalletAuthStatus> oWalletAuthStatus = createEnum("oWalletAuthStatus", WalletAuthStatus.class);

    public final StringPath sEmail = createString("sEmail");

    public final StringPath sProfileImage = createString("sProfileImage");

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

