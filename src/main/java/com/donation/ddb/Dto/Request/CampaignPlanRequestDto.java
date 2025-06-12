package com.donation.ddb.Dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CampaignPlanRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JoinDto {

        @JsonProperty("title")
        @NotBlank(message = "캠페인 계획 제목은 필수입니다.")
        private String title;

        @JsonProperty("amount")
        @NotNull(message = "캠페인 계획 금액은 필수입니다.")
        private Integer amount;
    }
}