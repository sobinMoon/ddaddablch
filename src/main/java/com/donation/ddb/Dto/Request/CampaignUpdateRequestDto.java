package com.donation.ddb.Dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

public class CampaignUpdateRequestDto {

    @Getter
    @Builder
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinDto {
        @JsonProperty("title")
        @NotEmpty(message = "캠페인 제목은 필수입니다.")
        private String title;

        @JsonProperty("content")
        @NotEmpty(message = "캠페인 내용은 필수입니다.")
        private String content;

        @JsonProperty("spendings")
        @Valid
        @NotEmpty(message = "최소 한 개 이상의 기부금 사용 내역이 필요합니다.")
        List<CampaignSpendingRequestDto.JoinDto> spendings;
    }
}
