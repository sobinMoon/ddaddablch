package com.donation.ddb.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStudentUser is a Querydsl query type for StudentUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudentUser extends EntityPathBase<StudentUser> {

    private static final long serialVersionUID = 1157364087L;

    public static final QStudentUser studentUser = new QStudentUser("studentUser");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath sEmail = createString("sEmail");

    public final NumberPath<Long> sId = createNumber("sId", Long.class);

    public final BooleanPath sIsActive = createBoolean("sIsActive");

    public final StringPath sName = createString("sName");

    public final StringPath sNickname = createString("sNickname");

    public final StringPath sPassword = createString("sPassword");

    public final StringPath sProfileImage = createString("sProfileImage");

    public final StringPath sWalletAddress = createString("sWalletAddress");

    public final EnumPath<WalletAuthStatus> sWalletAuthStatus = createEnum("sWalletAuthStatus", WalletAuthStatus.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QStudentUser(String variable) {
        super(StudentUser.class, forVariable(variable));
    }

    public QStudentUser(Path<? extends StudentUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStudentUser(PathMetadata metadata) {
        super(StudentUser.class, metadata);
    }

}

