package com.donation.ddb.Dto.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignCommentListResponseDto {

    @JsonProperty("totalElements")
    private Long count;

    @JsonProperty("comments")
    private List<CampaignCommentResponseDto> comments;

    @JsonProperty("totalPages")
    private Long totalPages;

    public static CampaignCommentListResponseDto from(Long count, List<CampaignCommentResponseDto> comments) {
        return CampaignCommentListResponseDto.builder()
                .count(count)
                .comments(comments)
                .totalPages((long) Math.ceil((double) count / comments.size()))
                .build();
    }

}
