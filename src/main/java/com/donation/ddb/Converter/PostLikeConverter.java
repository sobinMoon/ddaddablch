package com.donation.ddb.Converter;

import com.donation.ddb.Domain.Post;
import com.donation.ddb.Domain.PostLike;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Response.PostLikeResponseDto;

public class PostLikeConverter {
    public static PostLike toPostLike(Post post, StudentUser user) {
        return PostLike.builder()
                .post(post)
                .studentUser(user)
                .build();
    }

    public static PostLikeResponseDto.JoinResultDto toJoinResultDto(PostLike postLike) {
        return PostLikeResponseDto.JoinResultDto.builder()
                .postLikeId(postLike.getPlId())
                .createdAt(postLike.getCreatedAt())
                .build();
    }

}
