package com.donation.ddb.Service.PostLikeService;

import com.donation.ddb.Converter.PostLikeConverter;
import com.donation.ddb.Domain.Post;
import com.donation.ddb.Domain.PostLike;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Request.PostLikeRequestDto;
import com.donation.ddb.Repository.PostLikeRepository;
import com.donation.ddb.Repository.PostRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import com.donation.ddb.Service.PostService.PostQueryService;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.handler.PostHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeCommandService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final StudentUserRepository studentUserRepository;

    public PostLike joinPostLike(Long pId, String sEmail) {
        Post post = postRepository.findPostBypId(pId);
        StudentUser studentUser = studentUserRepository.findBysEmail(sEmail)
                .orElseThrow(() -> new PostHandler(ErrorStatus.STUDENT_USER_NOT_FOUND));
        Optional<PostLike> existPostLike = postLikeRepository.findByPost_pIdAndStudentUser_sEmail(pId, sEmail);

        if (existPostLike.isPresent()) {
            postLikeRepository.delete(existPostLike.get());
            return existPostLike.get();
        } else {
            return postLikeRepository.save(PostLikeConverter.toPostLike(post, studentUser));
        }
    }
}
