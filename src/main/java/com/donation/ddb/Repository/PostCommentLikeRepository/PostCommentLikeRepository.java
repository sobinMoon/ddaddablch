package com.donation.ddb.Repository.PostCommentLikeRepository;

import com.donation.ddb.Domain.PostCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PostCommentLikeRepository extends JpaRepository<PostCommentLike, Long> {
    Optional<PostCommentLike> findByPostComment_pcIdAndStudentUser_sEmail(Long postId, String userEmail);

    @Query("SELECT p.postComment.pcId FROM PostCommentLike p WHERE p.studentUser.sEmail = :email AND p.postComment.pcId IN :commentIds")
    Set<Long> findLikedCommentIdsByEmailAndCommentIds(@Param("email") String email, @Param("commentIds") List<Long> commentIds);

}
