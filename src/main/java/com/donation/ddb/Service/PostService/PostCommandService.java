package com.donation.ddb.Service.PostService;

import com.donation.ddb.Converter.PostConverter;
import com.donation.ddb.Domain.Post;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Request.PostRequestDto;
import com.donation.ddb.Repository.PostRepository.PostRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.handler.PostHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostCommandService {
    private final PostRepository postRepository;
    private final StudentUserRepository studentUserRepository;

    public Post addCampaign(PostRequestDto.JoinDto joinDto, String userEmail) {
        StudentUser user = studentUserRepository.findBysEmail(userEmail)
                .orElseThrow(() -> new PostHandler(ErrorStatus.STUDENT_USER_NOT_FOUND));

        Post newPost = PostConverter.toPost(joinDto, user);

        return postRepository.save(newPost);
    }

    public Post updateCampaign(Post post) {
        return postRepository.save(post);
    }

    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostHandler(ErrorStatus.POST_NOT_FOUND);
        }
        postRepository.deleteById(postId);
    }
}
