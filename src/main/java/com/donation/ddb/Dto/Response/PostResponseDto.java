package com.donation.ddb.Dto.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class JoinResultDto {
        private Long postId;
        private LocalDateTime createdAt;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class PreviewDto {
        private Long postId;
        private String title;
        private String previewContent;
        private String nft;
        private Long likeCount;
        private Long commentCount;
        private StudentUserResponse.StudentUserCommentDto studentUser;
        private LocalDateTime createdAt;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class PreviewListDto {
        private List<PreviewDto> postList;
        private Integer listSize;
        private Integer totalPage;
        private Long totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class DetailDto {
        private Long postId;
        private String title;
        private String content;
        private String nft;
        private Long likeCount;
        private Long commentCount;
        private Boolean liked;
        private StudentUserResponse.StudentUserCommentDto studentUser;
        private LocalDateTime createdAt;
    }
}
