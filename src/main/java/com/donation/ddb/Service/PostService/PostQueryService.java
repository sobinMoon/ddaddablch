package com.donation.ddb.Service.PostService;

import com.donation.ddb.Domain.Post;
import com.donation.ddb.Repository.PostRepository.PostRepository;
import com.donation.ddb.Repository.projection.PostWithCount;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.handler.PostHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {
    private final PostRepository postRepository;

    public boolean existsBypId(Long postId) {
        return postRepository.existsById(postId);
    }

    public PostWithCount findPostWithCountByPId(Long postId) {
        return postRepository.findPostWithCountByPId(postId);
    }

    public Page<PostWithCount> getPostList(Integer page) {
        return postRepository.findPostListCustom(PageRequest.of(page, 8));
    }

    public Post findPostByPId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_FOUND));
    }

}
