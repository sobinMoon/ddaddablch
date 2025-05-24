package com.donation.ddb.Repository.PostRepository;

import com.donation.ddb.Repository.projection.PostWithCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<PostWithCount> findPostListCustom(Pageable pageable);
    PostWithCount findPostWithCountByPId(Long postId);
}
