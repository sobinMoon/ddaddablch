package com.donation.ddb.Controller;

import com.donation.ddb.Converter.PostCommentConverter;
import com.donation.ddb.Converter.PostCommentLikeConverter;
import com.donation.ddb.Converter.PostConverter;
import com.donation.ddb.Converter.PostLikeConverter;
import com.donation.ddb.Domain.*;
import com.donation.ddb.Dto.Request.PostCommentRequestDto;
import com.donation.ddb.Dto.Request.PostRequestDto;
import com.donation.ddb.Dto.Response.PostCommentResponseDto;
import com.donation.ddb.Dto.Response.PostResponseDto;
import com.donation.ddb.Repository.projection.PostCommentWithUser;
import com.donation.ddb.Repository.projection.PostWithCount;
import com.donation.ddb.Service.NotificationService;
import com.donation.ddb.Service.PostCommentLikeService.PostCommentLikeCommandService;
import com.donation.ddb.Service.PostCommentLikeService.PostCommentLikeQueryService;
import com.donation.ddb.Service.PostCommentService.PostCommentCommandService;
import com.donation.ddb.Service.PostCommentService.PostCommentQueryService;
import com.donation.ddb.Service.PostLikeService.PostLikeCommandService;
import com.donation.ddb.Service.PostLikeService.PostLikeQueryService;
import com.donation.ddb.Service.PostService.PostCommandService;
import com.donation.ddb.Service.PostService.PostQueryService;
import com.donation.ddb.apiPayload.ApiResponse;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.handler.CampaignHandler;
import com.donation.ddb.validation.ExistPost;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/posts")
@NoArgsConstructor
@Slf4j
@Validated
public class PostController {

    @Autowired
    private PostCommandService postCommandService;

    @Autowired
    private PostLikeCommandService postLikeCommandService;
    @Autowired
    private PostLikeQueryService postLikeQueryService;
    @Autowired
    private PostCommentCommandService postCommentCommandService;
    @Autowired
    private PostCommentLikeCommandService postCommentLikeCommandService;
    @Autowired
    private PostCommentLikeQueryService postCommentLikeQueryService;
    @Autowired
    private PostQueryService postQueryService;
    @Autowired
    private PostCommentQueryService postCommentQueryService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("")
    public ApiResponse<?> addPost(
            @RequestBody @Valid PostRequestDto.JoinDto joinDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        if (userDetails == null) {
            throw new CampaignHandler(ErrorStatus._UNAUTHORIZED);
        }

        if (userDetails.isOrganization()) {
            throw new CampaignHandler(ErrorStatus._FORBIDDEN);
        }

        String email = userDetails.getUsername();

        Post newPost = postCommandService.addCampaign(joinDto, email);

        return ApiResponse.onSuccess(PostConverter.toJoinResultDto(newPost));
    }

    @PostMapping("/{postId}/likes")
    public ApiResponse<?> addPostLike(
            @PathVariable(value="postId") @ExistPost Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new CampaignHandler(ErrorStatus._UNAUTHORIZED);
        }

        if (userDetails.isOrganization()) {
            throw new CampaignHandler(ErrorStatus._FORBIDDEN);
        }

        String email = userDetails.getUsername();

        PostLike postLike = postLikeCommandService.joinPostLike(postId, email);

        return ApiResponse.onSuccess(PostLikeConverter.toJoinResultDto(postLike));
    }

    @PostMapping("/{postId}/comments")
    public ApiResponse<?> addPostComment(
            @PathVariable(value="postId") @ExistPost Long postId,
            @RequestBody @Valid PostCommentRequestDto.JoinDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new CampaignHandler(ErrorStatus._UNAUTHORIZED);
        }

        if (userDetails.isOrganization()) {
            throw new CampaignHandler(ErrorStatus._FORBIDDEN);
        }

        String email = userDetails.getUsername();


        PostComment postComment = postCommentCommandService.addPostComment(request, postId, email);
        // 알림 생성 코드 추가
        try {
            // 게시글 정보 조회
            PostWithCount post = postQueryService.findPostWithCountByPId(postId);

            // 댓글 작성자 정보
            String commenterName = userDetails.getNickname();

            // 게시글 작성자와 댓글 작성자가 다른 경우에만 알림 전송
            if (!post.getStudentUser().getSEmail().equals(email)) {
                // 게시글 작성자의 ID 가져오기
                Long postAuthorId = post.getStudentUser().getSId();

                notificationService.createCommentNotification(
                        postAuthorId,        // 게시글 작성자 ID
                        commenterName,       // 댓글 작성자 이름
                        postId              // 게시글 ID
                );

                log.info("댓글 알림 전송 완료: Post {} -> Author {}", postId, postAuthorId);
            }
        } catch (Exception e) {
            // 알림 생성 실패해도 댓글 생성은 성공으로 처리
            log.error("댓글 알림 생성 실패", e);
        }
        return ApiResponse.onSuccess(PostCommentConverter.toJoinResultDto(postComment));
    }

    @PostMapping("/{postId}/comments/{commentId}/likes")
    public ApiResponse<?> addPostCommentLike(
            @PathVariable(value="postId") @ExistPost Long postId,
            @PathVariable(value="commentId") Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new CampaignHandler(ErrorStatus._UNAUTHORIZED);
        }

        if (userDetails.isOrganization()) {
            throw new CampaignHandler(ErrorStatus._FORBIDDEN);
        }

        String email = userDetails.getUsername();

        PostCommentLike postCommentlike = postCommentLikeCommandService.togglePostCommentLike(commentId, email);

        return ApiResponse.onSuccess(PostCommentLikeConverter.toJoinResultDto(postCommentlike));
    }

    @GetMapping("")
    public ApiResponse<PostResponseDto.PreviewListDto> getPostList(
            @RequestParam(value = "page", defaultValue = "0") Integer page
    ) {

        if (page < 0) {
            throw new CampaignHandler(ErrorStatus.PAGE_NUMBER_INVALID);
        }

        Page<PostWithCount> postList = postQueryService.getPostList(page);

        return ApiResponse.onSuccess(PostConverter.toPreviewListDto(postList));
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostResponseDto.DetailDto> getPostDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(value="postId") @ExistPost Long postId
    ) {
        Boolean liked = false;

        if (userDetails != null && userDetails.isStudent()) {
            String email = userDetails.getUsername();
            liked = postLikeQueryService.existsByPostAndStudentUser(postId, email);
        }

        PostWithCount post = postQueryService.findPostWithCountByPId(postId);

        return ApiResponse.onSuccess(PostConverter.toDetailDto(post, liked));
    }

    @GetMapping("/{postId}/comments")
    public ApiResponse<List<PostCommentResponseDto.ListDto>> getPostCommentList(
            @PathVariable(value="postId") @ExistPost Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String email = null;
        if (userDetails != null && userDetails.isStudent()) {
            email = userDetails.getUsername();
        }

        List<PostCommentResponseDto.ListDto> postCommentList = postCommentQueryService.getPostCommentList(email, postId);

        return ApiResponse.onSuccess(postCommentList);
    }
}