package com.donation.ddb.Service.PostCommentService;

import com.donation.ddb.Converter.PostCommentConverter;
import com.donation.ddb.Converter.PostConverter;
import com.donation.ddb.Domain.Post;
import com.donation.ddb.Domain.PostComment;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Request.PostCommentRequestDto;
import com.donation.ddb.Repository.PostCommentRepository;
import com.donation.ddb.Repository.PostRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.handler.PostHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCommentCommandService {
    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final StudentUserRepository studentUserRepository;

    public PostComment addPostComment(PostCommentRequestDto.JoinDto joinDto, Long postId, String userEmail) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_FOUND));
        StudentUser user = studentUserRepository.findBysEmail(userEmail)
                .orElseThrow(() -> new PostHandler(ErrorStatus.STUDENT_USER_NOT_FOUND));

        return postCommentRepository.save(PostCommentConverter.toPostComment(joinDto, user, post));
    }
}
