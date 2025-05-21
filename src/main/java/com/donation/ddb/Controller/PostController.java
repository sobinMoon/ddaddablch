package com.donation.ddb.Controller;

import com.donation.ddb.Converter.PostCommentConverter;
import com.donation.ddb.Converter.PostConverter;
import com.donation.ddb.Converter.PostLikeConverter;
import com.donation.ddb.Domain.Post;
import com.donation.ddb.Domain.PostComment;
import com.donation.ddb.Domain.PostLike;
import com.donation.ddb.Dto.Request.PostCommentRequestDto;
import com.donation.ddb.Dto.Request.PostRequestDto;
import com.donation.ddb.Service.PostCommentService.PostCommentCommandService;
import com.donation.ddb.Service.PostLikeService.PostLikeCommandService;
import com.donation.ddb.Service.PostService.PostCommandService;
import com.donation.ddb.apiPayload.ApiResponse;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.handler.CampaignHandler;
import com.donation.ddb.validation.ExistPost;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    private PostCommentCommandService postCommentCommandService;

    @PostMapping("")
    public ApiResponse<?> addPost(
            @RequestBody @Valid PostRequestDto.JoinDto joinDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        if (userDetails == null) {
            throw new CampaignHandler(ErrorStatus._UNAUTHORIZED);
        }

        String email = userDetails.getUsername();

        Post newPost = postCommandService.addCampaign(joinDto, email);

        return ApiResponse.onSuccess(PostConverter.toJoinResultDto(newPost));
    }

    @PostMapping("/{postId}/likes")
    public ApiResponse<?> addPostLike(
            @PathVariable(value="postId") @ExistPost Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new CampaignHandler(ErrorStatus._UNAUTHORIZED);
        }

        String email = userDetails.getUsername();

        PostLike postLike = postLikeCommandService.joinPostLike(postId, email);

        return ApiResponse.onSuccess(PostLikeConverter.toJoinResultDto(postLike));
    }

    @PostMapping("/{postId}/comments")
    public ApiResponse<?> addPostComment(
            @PathVariable(value="postId") @ExistPost Long postId,
            @RequestBody @Valid PostCommentRequestDto.JoinDto request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new CampaignHandler(ErrorStatus._UNAUTHORIZED);
        }

        String email = userDetails.getUsername();

        PostComment postComment = postCommentCommandService.addPostComment(request, postId, email);

        return ApiResponse.onSuccess(PostCommentConverter.toJoinResultDto(postComment));
    }
}
