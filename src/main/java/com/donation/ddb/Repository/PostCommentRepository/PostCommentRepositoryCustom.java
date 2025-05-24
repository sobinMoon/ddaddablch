package com.donation.ddb.Repository.PostCommentRepository;

import com.donation.ddb.Repository.projection.PostCommentWithUser;

import java.util.List;

public interface PostCommentRepositoryCustom {
    List<PostCommentWithUser> findPostListWithUser(Long postId);
}
