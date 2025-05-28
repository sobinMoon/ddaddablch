package com.donation.ddb.Repository.PostCommentRepository;

import com.donation.ddb.Domain.QPost;
import com.donation.ddb.Domain.QPostComment;
import com.donation.ddb.Domain.QPostCommentLike;
import com.donation.ddb.Dto.Response.PostCommentResponseDto;
import com.donation.ddb.Repository.projection.PostCommentWithUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class PostCommentRepositoryImpl implements PostCommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QPost post = QPost.post;

    @Override
    public List<PostCommentWithUser> findPostListWithUser(Long postId) {
        QPostComment postComment = QPostComment.postComment;
        QPostCommentLike postCommentLike = QPostCommentLike.postCommentLike;

        List<PostCommentWithUser> results = jpaQueryFactory
                .select(Projections.constructor(
                        PostCommentWithUser.class,
                        postComment,
                        postComment.studentUser,
                        JPAExpressions
                                .select(postCommentLike.count())
                                .from(postCommentLike)
                                .where(postCommentLike.postComment.eq(postComment))
                ))
                .from(postComment)
                .orderBy(post.createdAt.desc())
                .fetch();

        return results;
    }
}
