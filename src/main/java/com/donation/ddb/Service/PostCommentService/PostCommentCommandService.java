package com.donation.ddb.Service.PostCommentService;

import com.donation.ddb.Converter.PostCommentConverter;
import com.donation.ddb.Domain.Post;
import com.donation.ddb.Domain.PostComment;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Request.PostCommentRequestDto;
import com.donation.ddb.Repository.NotificationRepository;
import com.donation.ddb.Repository.PostCommentRepository.PostCommentRepository;
import com.donation.ddb.Repository.PostRepository.PostRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import com.donation.ddb.Service.NotificationService;
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
    private final NotificationService notificationService;

    public PostComment addPostComment(PostCommentRequestDto.JoinDto joinDto, Long postId, String userEmail) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_FOUND));

        StudentUser user = studentUserRepository.findBysEmail(userEmail)
                .orElseThrow(() -> new PostHandler(ErrorStatus.STUDENT_USER_NOT_FOUND));

        PostComment savedpostComment=postCommentRepository.save(PostCommentConverter.toPostComment(joinDto, user, post));

//        // 알림 생성 (자기 글에 자기가 댓글 단 경우 제외)
//
//        if (!post.getStudentUser().getSId().equals(savedpostComment.getStudentUser().getSId())) {
//            notificationService.createCommentNotification(
//                    post.getStudentUser().getSId(),    // 게시글 작성자 ID
//                    savedpostComment.getStudentUser().getSNickname(),        // 댓글 작성자 닉네임
//                    post.getPId()                      // 게시글 ID
//            );
//        }

        return savedpostComment;
    }
}
