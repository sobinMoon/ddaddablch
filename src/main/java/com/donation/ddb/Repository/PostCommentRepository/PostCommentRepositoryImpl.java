package com.donation.ddb.Repository.PostCommentRepository;

import com.donation.ddb.Domain.PostComment;
import com.donation.ddb.Domain.QPost;
import com.donation.ddb.Domain.QPostComment;
import com.donation.ddb.Domain.QPostCommentLike;
import com.donation.ddb.Dto.Response.PostCommentResponseDto;
import com.donation.ddb.Dto.Response.StudentMyPageResponseDTO;
import com.donation.ddb.Repository.projection.PostCommentWithUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.donation.ddb.Domain.QPostCommentLike.postCommentLike;

@Repository
@RequiredArgsConstructor
public class PostCommentRepositoryImpl implements PostCommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QPost post = QPost.post;
    private final QPostComment postComment=QPostComment.postComment;
    private final QPostCommentLike postCommentLik= postCommentLike;


    //특정 게시글의 댓글 사용자 정보와 함께 조회
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
                .where(postComment.post.pId.eq(postId))
                .orderBy(postComment.createdAt.desc())
                .fetch();

        return results;
    }

    @Override
    public List<StudentMyPageResponseDTO.PostCommentDTO> findRecentCommentsByStudentId(Long sId) {
        return jpaQueryFactory
                .select(Projections.constructor(
                        StudentMyPageResponseDTO.PostCommentDTO.class,
                        postComment.pcId,
                        postComment.pcContent,
                        postComment.createdAt,
                        postComment.post.pId,
                        JPAExpressions
                                .select(postCommentLike.count())
                                .from(postCommentLike)
                                .where(postCommentLike.postComment.eq(postComment))
                ))
                .from(postComment)
                .where(postComment.studentUser.sId.eq(sId))
                .orderBy(postComment.createdAt.desc())
                .limit(5)
                .fetch();
    }

    @Override
    public Long countLikesByCommentId(Long pcId){
        QPostCommentLike postCommentLike= QPostCommentLike.postCommentLike;

        Long count = jpaQueryFactory
                .select(postCommentLike.count())
                .from(postCommentLike)
                .where(postCommentLike.postComment.pcId.eq(pcId))
                .fetchOne();
        return count != null ? count.intValue() : 0L;
    }

}
