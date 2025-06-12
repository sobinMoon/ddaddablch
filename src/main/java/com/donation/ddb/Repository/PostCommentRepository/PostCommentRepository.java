package com.donation.ddb.Repository.PostCommentRepository;

import com.donation.ddb.Domain.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long>, PostCommentRepositoryCustom {
}
