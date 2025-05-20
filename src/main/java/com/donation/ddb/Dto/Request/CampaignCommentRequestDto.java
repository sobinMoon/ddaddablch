package com.donation.ddb.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CampaignCommentRequestDto {
    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;
}
