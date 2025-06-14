package com.donation.ddb.Repository.PostRepository;

import com.donation.ddb.Domain.Post;
import com.donation.ddb.Repository.projection.PostWithCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    Post findPostBypId(Long postId);
}
