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
}
