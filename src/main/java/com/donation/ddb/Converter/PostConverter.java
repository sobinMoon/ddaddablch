package com.donation.ddb.Converter;

import com.donation.ddb.Domain.Post;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Request.PostRequestDto;
import com.donation.ddb.Dto.Response.PostResponseDto;

public class PostConverter {
    public static Post toPost(PostRequestDto.JoinDto joinDto, StudentUser user) {
        return Post.builder()
                .pTitle(joinDto.getTitle())
                .pContent(joinDto.getContent())
                .pNft(joinDto.getNft())
                .studentUser(user)
                .build();
    }

    public static PostResponseDto.JoinResultDto toJoinResultDto(Post post) {
        return PostResponseDto.JoinResultDto.builder()
                .postId(post.getPId())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
