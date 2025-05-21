package com.donation.ddb.Repository;

import com.donation.ddb.Domain.PostComment;
import com.donation.ddb.Domain.PostCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
}
