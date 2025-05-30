package com.donation.ddb.Service.PostCommentService;

import com.donation.ddb.Converter.PostCommentConverter;
import com.donation.ddb.Domain.PostComment;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Response.PostCommentResponseDto;
import com.donation.ddb.Repository.PostCommentLikeRepository.PostCommentLikeRepository;
import com.donation.ddb.Repository.PostCommentRepository.PostCommentRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import com.donation.ddb.Repository.projection.PostCommentWithUser;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.handler.PostHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCommentQueryService {

    private final PostCommentRepository postCommentRepository;
    private final PostCommentLikeRepository postCommentLikeRepository;
    private final StudentUserRepository studentUserRepository;

    public List<PostCommentResponseDto.ListDto> getPostCommentList(String email, Long postId) {
        List <PostCommentWithUser> postCommentWithUsers = postCommentRepository.findPostListWithUser(postId);

        List<Long> commentIds = postCommentWithUsers.stream()
                .map(postCommentWithUser -> postCommentWithUser.getPostComment().getPcId())
                .toList();

        Set<Long> likedCommentIds = new HashSet<>();

        log.info(">> 요청받은 userEmail : " + email);
        if (email != null) {
            StudentUser user = studentUserRepository.findBysEmail(email)
                    .orElseThrow(() -> new PostHandler(ErrorStatus.STUDENT_USER_NOT_FOUND));

            likedCommentIds = postCommentLikeRepository.findLikedCommentIdsByEmailAndCommentIds(user.getSEmail(), commentIds);
        }

        final Set<Long> likedCommentIdsFinal = likedCommentIds;

        // 이것들을 PostCommentWithUser에 추가
        List<PostCommentResponseDto.ListDto> updatedPostComments = postCommentWithUsers.stream()
                .map(postCommentWithUser -> {
                    PostComment postComment = postCommentWithUser.getPostComment();
                    boolean isLiked = likedCommentIdsFinal.contains(postComment.getPcId());
                    return PostCommentConverter.toListItemDto(postCommentWithUser, isLiked);
                })
                .toList();

        return updatedPostComments;
    }
}
