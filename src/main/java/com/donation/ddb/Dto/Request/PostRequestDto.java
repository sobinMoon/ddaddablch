package com.donation.ddb.Dto.Request;

import com.donation.ddb.Domain.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PostRequestDto {

    @NoArgsConstructor
    @Getter
    @Setter
    public static class JoinDto {
        @JsonProperty("title")
        @Size(min = 1, max = 100, message = "제목은 1자 이상 100자 이하로 입력해주세요.")
        private String title;

        @JsonProperty("content")
        @Size(min = 1, max = 5000, message = "내용은 1자 이상 5000자 이하로 입력해주세요.")
        private String content;

        @JsonProperty("nft")
        private String imagePath;
    }

    @NoArgsConstructor
    @Getter
    public static class PreviewListDto {

    }
}
