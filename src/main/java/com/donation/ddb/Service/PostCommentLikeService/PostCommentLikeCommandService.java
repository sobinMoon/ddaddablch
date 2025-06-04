package com.donation.ddb.Service.PostCommentLikeService;

import com.donation.ddb.Converter.PostCommentLikeConverter;
import com.donation.ddb.Domain.PostComment;
import com.donation.ddb.Domain.PostCommentLike;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Repository.PostCommentLikeRepository.PostCommentLikeRepository;
import com.donation.ddb.Repository.PostCommentRepository.PostCommentRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import com.donation.ddb.apiPayload.exception.handler.PostHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostCommentLikeCommandService {
    private final PostCommentLikeRepository postCommentLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final StudentUserRepository studentUserRepository;

    public PostCommentLike togglePostCommentLike(Long postCommentId, String userEmail) {

        PostComment postComment = postCommentRepository.findById(postCommentId)
                .orElseThrow(() -> new PostHandler(ErrorStatus.POST_COMMENT_NOT_FOUND));

        StudentUser user = studentUserRepository.findBysEmail(userEmail)
                .orElseThrow(() -> new PostHandler(ErrorStatus.STUDENT_USER_NOT_FOUND));

        if (postComment.getStudentUser().getSEmail().equals(userEmail)) {
            throw new PostHandler(ErrorStatus.POST_COMMENT_LIKE_SELF);
        }

        Optional<PostCommentLike> existingCommentLike = postCommentLikeRepository.findByPostComment_pcIdAndStudentUser_sEmail(postCommentId, userEmail);

        if (existingCommentLike.isPresent()) {
            postCommentLikeRepository.delete(existingCommentLike.get());
            return existingCommentLike.get();
        } else {
            return postCommentLikeRepository.save(PostCommentLikeConverter.toPostCommentLike(user, postComment));
        }
    }
}
