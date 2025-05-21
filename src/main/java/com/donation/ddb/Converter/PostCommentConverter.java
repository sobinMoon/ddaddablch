package com.donation.ddb.Converter;

import com.donation.ddb.Domain.Post;
import com.donation.ddb.Domain.PostComment;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Request.PostCommentRequestDto;
import com.donation.ddb.Dto.Response.PostCommentResponseDto;

public class PostCommentConverter {

    public static PostCommentResponseDto.JoinResultDto toJoinResultDto(PostComment postComment) {
        return PostCommentResponseDto.JoinResultDto.builder()
                .postCommentId(postComment.getPcId())
                .createdAt(postComment.getCreatedAt())
                .build();
    }

    public static PostComment toPostComment(
            PostCommentRequestDto.JoinDto postCommentRequestDto,
            StudentUser user,
            Post post
    ) {
        return PostComment.builder()
                .post(post)
                .studentUser(user)
                .pcContent(postCommentRequestDto.getContent())
                .build();
    }
}
