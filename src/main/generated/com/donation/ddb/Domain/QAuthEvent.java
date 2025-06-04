package com.donation.ddb.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuthEvent is a Querydsl query type for AuthEvent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuthEvent extends EntityPathBase<AuthEvent> {

    private static final long serialVersionUID = -284976413L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuthEvent authEvent = new QAuthEvent("authEvent");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final EnumPath<WalletAuthStatus> eventType = createEnum("eventType", WalletAuthStatus.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath message = createString("message");

    public final StringPath nonce = createString("nonce");

    public final StringPath signature = createString("signature");

    public final QStudentUser user;

    public final StringPath walletAddress = createString("walletAddress");

    public QAuthEvent(String variable) {
        this(AuthEvent.class, forVariable(variable), INITS);
    }

    public QAuthEvent(Path<? extends AuthEvent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuthEvent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuthEvent(PathMetadata metadata, PathInits inits) {
        this(AuthEvent.class, metadata, inits);
    }

    public QAuthEvent(Class<? extends AuthEvent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QStudentUser(forProperty("user")) : null;
    }

}

