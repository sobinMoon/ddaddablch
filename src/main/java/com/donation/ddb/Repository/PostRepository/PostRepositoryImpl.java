package com.donation.ddb.Repository.PostRepository;

import com.donation.ddb.Domain.QPost;
import com.donation.ddb.Domain.QPostComment;
import com.donation.ddb.Domain.QPostLike;
import com.donation.ddb.Dto.Response.StudentMyPageResponseDTO;
import com.donation.ddb.Repository.projection.PostWithCount;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.donation.ddb.Domain.QPostComment.postComment;
import static com.donation.ddb.Domain.QPostLike.postLike;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QPost post = QPost.post;

    @Override
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
                        post.studentUser,
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

    // 공통 SELECT 절을 만드는 메서드
    private JPAQuery<PostWithCount> createPostWithCountQuery() {
        QPostLike postLike = QPostLike.postLike;
        QPostComment postComment = QPostComment.postComment;

        return jpaQueryFactory
                .select(Projections.constructor(
                        PostWithCount.class,
                        post.pId,
                        post.pTitle,
                        post.pContent,
                        post.pNft,
                        JPAExpressions.select(postLike.count()).from(postLike).where(postLike.post.eq(post)),
                        JPAExpressions.select(postComment.count()).from(postComment).where(postComment.post.eq(post)),
                        post.studentUser,
                        post.createdAt
                ))
                .from(post);
    }

    // 기존 메서드
    @Override
    public PostWithCount findPostWithCountByPId(Long postId) {
        return createPostWithCountQuery()
                .where(post.pId.eq(postId))
                .fetchOne();
    }

    // 새 메서드
    @Override
    public List<StudentMyPageResponseDTO.RecentPostDTO> findRecentPostsByStudentId(Long sId) {
//        return createPostWithCountQuery()
//                .where(post.studentUser.sId.eq(sId))
//                .orderBy(post.createdAt.desc())
//                .fetch();
          return jpaQueryFactory
                  .select(
                          Projections.constructor(
                                  StudentMyPageResponseDTO.RecentPostDTO.class,
                                  post.pId,
                                  post.pTitle,
                                  post.createdAt,
                                  post.pNft,
                                  JPAExpressions.select(postLike.count()).from(postLike).where(postLike.post.eq(post)),     // likeCount
                                  JPAExpressions.select(postComment.count()).from(postComment).where(postComment.post.eq(post)) // commentCount
                          ))
                  .from(post)
                  .where(post.studentUser.sId.eq(sId))
                  .orderBy(post.createdAt.desc())
                  .fetch();
    }

}
