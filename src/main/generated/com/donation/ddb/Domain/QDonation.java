package com.donation.ddb.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDonation is a Querydsl query type for Donation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDonation extends EntityPathBase<Donation> {

    private static final long serialVersionUID = -5540767L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDonation donation = new QDonation("donation");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final QCampaign campaign;

    public final StringPath campaignWalletAddress = createString("campaignWalletAddress");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> dId = createNumber("dId", Long.class);

    public final StringPath donorWalletAddress = createString("donorWalletAddress");

    public final StringPath message = createString("message");

    public final NumberPath<Long> nftTokenId = createNumber("nftTokenId", Long.class);

    public final EnumPath<DonationStatus> status = createEnum("status", DonationStatus.class);

    public final QStudentUser studentUser;

    public final StringPath transactionHash = createString("transactionHash");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QDonation(String variable) {
        this(Donation.class, forVariable(variable), INITS);
    }

    public QDonation(Path<? extends Donation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDonation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDonation(PathMetadata metadata, PathInits inits) {
        this(Donation.class, metadata, inits);
    }

    public QDonation(Class<? extends Donation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.campaign = inits.isInitialized("campaign") ? new QCampaign(forProperty("campaign"), inits.get("campaign")) : null;
        this.studentUser = inits.isInitialized("studentUser") ? new QStudentUser(forProperty("studentUser")) : null;
    }

}

