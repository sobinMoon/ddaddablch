package com.donation.ddb.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class PostCommentResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class JoinResultDto {
        private Long postCommentId;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ListDto {
        private Long postCommentId;
        private String content;
        private StudentUserResponse.StudentUserCommentDto studentUser;
        private Boolean liked;
        private Long likeCount;
        private LocalDateTime createdAt;
    }
}
