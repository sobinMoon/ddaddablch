package com.donation.ddb.Service.PostService;

import com.donation.ddb.Domain.Post;
import com.donation.ddb.Repository.PostRepository;
import com.donation.ddb.apiPayload.exception.handler.PostHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {
    private final PostRepository postRepository;

    public boolean existsBypId(Long postId) {
        return postRepository.existsById(postId);
    }

    public Post findPostByPId(Long postId) {
        return postRepository.findPostBypId(postId);
    }

}
