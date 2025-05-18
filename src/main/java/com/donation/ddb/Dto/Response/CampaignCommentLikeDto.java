package com.donation.ddb.Dto.Response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class CampaignCommentLikeDto {
    @Getter
    @Builder
    public static class JoinResultDto {
        private Long ccId;
        LocalDateTime createdAt;
    }

    // 삭제 시 응답
    @Getter
    @Builder
    public static class toggleResultDto {
        private Long ccId;
    }
}
