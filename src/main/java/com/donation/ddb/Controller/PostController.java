package com.donation.ddb.Controller;

import com.donation.ddb.Converter.PostConverter;
import com.donation.ddb.Domain.Post;
import com.donation.ddb.Dto.Request.PostRequestDto;
import com.donation.ddb.Service.PostService.PostCommandService;
import com.donation.ddb.apiPayload.ApiResponse;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.handler.CampaignHandler;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@NoArgsConstructor
@Slf4j
public class PostController {

    @Autowired
    private PostCommandService postCommandService;

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
}
