package com.donation.ddb.Repository;

import com.donation.ddb.Domain.Post;
import com.donation.ddb.Domain.PostLike;
import com.donation.ddb.Domain.StudentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPost_pIdAndStudentUser_sEmail(Long pId, String sEmail);
    Boolean existsByPost_pIdAndStudentUser_sEmail(Long postId, String email);
}
