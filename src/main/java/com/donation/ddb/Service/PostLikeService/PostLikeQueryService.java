package com.donation.ddb.Service.PostLikeService;

import com.donation.ddb.Domain.Post;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeQueryService {
    private final PostLikeRepository postLikeRepository;

    public Boolean existsByPostAndStudentUser(Long postId, String email) {
        return postLikeRepository.existsByPost_pIdAndStudentUser_sEmail(postId, email);
    }
}
