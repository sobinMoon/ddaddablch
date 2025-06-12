package com.donation.ddb.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class PostLikeResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class JoinResultDto {
        private Long postLikeId;
        private LocalDateTime createdAt;
    }
}
