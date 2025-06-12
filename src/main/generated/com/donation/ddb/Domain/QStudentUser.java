package com.donation.ddb.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStudentUser is a Querydsl query type for StudentUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudentUser extends EntityPathBase<StudentUser> {

    private static final long serialVersionUID = 1157364087L;

    public static final QStudentUser studentUser = new QStudentUser("studentUser");

    public final ListPath<CampaignComment, QCampaignComment> campaignCommentList = this.<CampaignComment, QCampaignComment>createList("campaignCommentList", CampaignComment.class, QCampaignComment.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final ListPath<Donation, QDonation> donationList = this.<Donation, QDonation>createList("donationList", Donation.class, QDonation.class, PathInits.DIRECT2);

    public final ListPath<StudentNFT, QStudentNFT> nftList = this.<StudentNFT, QStudentNFT>createList("nftList", StudentNFT.class, QStudentNFT.class, PathInits.DIRECT2);

    public final ListPath<PostCommentLike, QPostCommentLike> postCommentLikeList = this.<PostCommentLike, QPostCommentLike>createList("postCommentLikeList", PostCommentLike.class, QPostCommentLike.class, PathInits.DIRECT2);

    public final ListPath<PostComment, QPostComment> postCommentList = this.<PostComment, QPostComment>createList("postCommentList", PostComment.class, QPostComment.class, PathInits.DIRECT2);

    public final ListPath<PostLike, QPostLike> postLikeList = this.<PostLike, QPostLike>createList("postLikeList", PostLike.class, QPostLike.class, PathInits.DIRECT2);

    public final ListPath<Post, QPost> postList = this.<Post, QPost>createList("postList", Post.class, QPost.class, PathInits.DIRECT2);

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public final StringPath sEmail = createString("sEmail");

    public final NumberPath<Long> sId = createNumber("sId", Long.class);

    public final BooleanPath sIsActive = createBoolean("sIsActive");

    public final StringPath sName = createString("sName");

    public final StringPath sNickname = createString("sNickname");

    public final StringPath sPassword = createString("sPassword");

    public final StringPath sProfileImage = createString("sProfileImage");

    public final EnumPath<WalletAuthStatus> sWalletAuthStatus = createEnum("sWalletAuthStatus", WalletAuthStatus.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final StringPath walletAddresses = createString("walletAddresses");

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

