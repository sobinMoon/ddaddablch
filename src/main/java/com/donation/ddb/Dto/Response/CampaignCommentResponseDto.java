package com.donation.ddb.Dto.Response;

import com.donation.ddb.Domain.CampaignComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignCommentResponseDto {
    private Long id;
    private String content;
    private StudentUserResponse.StudentUserCommentDto studentUser;
    private Long likes;
    private boolean isLiked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CampaignCommentResponseDto from(CampaignComment campaignComment, Long likes, boolean isLiked) {
        return CampaignCommentResponseDto.builder()
                .id(campaignComment.getCcId())
                .content(campaignComment.getCcContent())
                .studentUser(StudentUserResponse.StudentUserCommentDto.from(campaignComment.getStudentUser()))
                .likes(likes)
                .isLiked(isLiked)
                .createdAt(campaignComment.getCreatedAt())
                .updatedAt(campaignComment.getUpdatedAt())
                .build();
    }

}
