package com.donation.ddb.Converter;

import com.donation.ddb.Domain.Post;
import com.donation.ddb.Domain.PostComment;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Request.PostCommentRequestDto;
import com.donation.ddb.Dto.Response.PostCommentResponseDto;
import com.donation.ddb.Repository.projection.PostCommentWithUser;

import java.util.List;
import java.util.Map;

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

    public static PostCommentResponseDto.ListDto toListItemDto(PostCommentWithUser postComment, Boolean liked) {
        return PostCommentResponseDto.ListDto.builder()
                .postCommentId(postComment.getPostComment().getPcId())
                .content(postComment.getPostComment().getPcContent())
                .studentUser(StudentUserConverter.toCommentDto(postComment.getStudentUser()))
                .liked(liked)
                .likeCount(postComment.getLikeCount())
                .createdAt(postComment.getPostComment().getCreatedAt())
                .build();
    }

//    public static List<PostCommentResponseDto.ListDto> toListDto(List<PostCommentWithUser> postComments) {
//        return postComments.stream()
//                .map(postCommentWithUser -> toListItemDto()
//                        postCommentWithUser,
//                        postCommentWithUser.getLiked() != null && postCommentWithUser.getLiked()
//                ))
//                .toList();
//    }
}