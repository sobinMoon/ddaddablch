package com.donation.ddb.Dto.Request;

import com.donation.ddb.Domain.Enums.CampaignCategory;
import com.donation.ddb.Domain.Enums.CampaignStatusFlag;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

public class CampaignRequestDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Setter
    public static class JoinDto {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        @NotBlank(message = "캠페인 이름은 필수입니다.")
        private String name;

        @JsonProperty("imageUrl")
//        @NotBlank(message = "캠페인 이미지 URL은 필수입니다.")
        private String imageUrl;

        @JsonProperty("description")
        @NotBlank(message = "캠페인 설명은 필수입니다.")
        private String description;

        @JsonProperty("goal")
        @NotNull(message = "캠페인 목표 금액은 필수입니다.")
        private Integer goal;

        @JsonProperty("walletAddress")
        @NotBlank(message = "지갑 주소는 필수입니다.")
        private String walletAddress;

        @JsonProperty("category")
        @NotNull(message = "캠페인 카테고리는 필수입니다.")
        private CampaignCategory category;

        @JsonProperty("donateStart")
        @NotNull(message = "기부 시작일은 필수입니다.")
        private LocalDate donateStart;

        @JsonProperty("donateEnd")
        @NotNull(message = "기부 종료일은 필수입니다.")
        private LocalDate donateEnd;

        @JsonProperty("businessStart")
        @NotNull(message = "사업 시작일은 필수입니다.")
        private LocalDate businessStart;

        @JsonProperty("businessEnd")
        @NotNull(message = "사업 종료일은 필수입니다.")
        private LocalDate businessEnd;

        @JsonProperty("plans")
        @Valid
        @NotEmpty(message = "최소 한 개 이상의 기부 계획이 필요합니다.")
        List<CampaignPlanRequestDto.JoinDto> plans;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateStatusDto {
        @JsonProperty("status")
        @NotNull(message = "변경할 캠페인 상태값은 필수입니다. (FUNDED, IN_PROGRESS, COMPLETED)")
        private CampaignStatusFlag status;
    }
}
