package com.donation.ddb.Repository.PostRepository;

import com.donation.ddb.Domain.QPost;
import com.donation.ddb.Domain.QPostComment;
import com.donation.ddb.Domain.QPostLike;
import com.donation.ddb.Repository.projection.PostWithCount;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QPost post = QPost.post;

    public Page<PostWithCount> findPostListCustom(Pageable pageable) {
        QPostLike postLike = QPostLike.postLike;
        QPostComment postComment = QPostComment.postComment;

        List<PostWithCount> results = jpaQueryFactory
                .select(Projections.constructor(
                        PostWithCount.class,
                        post.pId,
                        post.pTitle,
                        post.pContent,
                        post.pNft,
                        JPAExpressions
                                .select(postLike.count())
                                .from(postLike)
                                .where(postLike.post.eq(post)),
                        JPAExpressions
                                .select(postComment.count())
                                .from(postComment)
                                .where(postComment.post.eq(post)),
                        post.createdAt
                ))
                .from(post)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(post.count())
                .from(post)
                .fetchOne();

        return new PageImpl<>(results, pageable, Objects.requireNonNullElse(total, 0L));


    }

}
