package com.donation.ddb.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CampaignSpendingRequestDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinDto {

        @NotBlank(message = "내용 입력은 필수입니다.")
        @Size(max=50, message = "내용은 최대 50자까지 입력 가능합니다.")
        private String title;

        @NotNull(message = "금액 입력은 필수입니다.")
        private Integer amount;
    }
}
