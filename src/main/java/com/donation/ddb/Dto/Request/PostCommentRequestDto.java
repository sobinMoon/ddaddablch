package com.donation.ddb.Dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PostCommentRequestDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinDto {
        @JsonProperty("content")
        @Size(min=1, max=500, message = "댓글은 1자 이상 500자 이하로 작성해야 합니다.")
        private String content;
    }
}
