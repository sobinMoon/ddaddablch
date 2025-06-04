package com.donation.ddb.Service.PostCommentLikeService;

import com.donation.ddb.Repository.PostCommentLikeRepository.PostCommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostCommentLikeQueryService {
    private final PostCommentLikeRepository postCommentLikeRepository;

    public Set<Long> findLikedCommentIdsByEmailAndCommentIds(String email, List<Long> commentIds) {
        return postCommentLikeRepository.findLikedCommentIdsByEmailAndCommentIds(email, commentIds);
    };

}
