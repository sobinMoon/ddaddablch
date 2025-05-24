package com.donation.ddb.Service.PostCommentService;

import com.donation.ddb.Domain.PostComment;
import com.donation.ddb.Repository.PostCommentRepository.PostCommentRepository;
import com.donation.ddb.Repository.projection.PostCommentWithUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentQueryService {

    private final PostCommentRepository postCommentRepository;

    public List<PostCommentWithUser> getPostCommentList(Long postId) {
        return postCommentRepository.findPostListWithUser(postId);
    }
}
