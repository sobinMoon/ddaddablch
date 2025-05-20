package com.donation.ddb.Dto.Request;

import com.donation.ddb.validation.ExistPost;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class PostLikeRequestDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class JoinDto {
        @JsonProperty("postId")
        @ExistPost
        private Long postId;

        @JsonProperty("studentId")
        private Long studentId;
    }
}
