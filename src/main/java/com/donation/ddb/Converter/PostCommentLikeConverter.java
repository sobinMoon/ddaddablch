package com.donation.ddb.Converter;

import com.donation.ddb.Domain.PostComment;
import com.donation.ddb.Domain.PostCommentLike;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Response.PostCommentLikeResponseDto;

public class PostCommentLikeConverter {
    public static PostCommentLikeResponseDto.JoinResultDto toJoinResultDto(PostCommentLike postCommentLike) {
        return PostCommentLikeResponseDto.JoinResultDto.builder()
                .postCommentLikeId(postCommentLike.getPclId())
                .createdAt(postCommentLike.getCreatedAt())
                .build();
    }

    public static PostCommentLike toPostCommentLike(StudentUser user, PostComment postComment) {
        return PostCommentLike.builder()
                .studentUser(user)
                .postComment(postComment)
                .build();
    }
}
