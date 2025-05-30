package com.donation.ddb.Dto.Request;

import com.donation.ddb.Domain.CampaignCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CampaignRequestDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JoinDto {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("imageUrl")
        private String imageUrl;

        @JsonProperty("description")
        private String description;

        @JsonProperty("goal")
        private Integer goal;

        @JsonProperty("walletAddress")
        private String walletAddress;

        @JsonProperty("category")
        private CampaignCategory category;

        @JsonProperty("donateStart")
        private LocalDate donateStart;

        @JsonProperty("donateEnd")
        private LocalDate donateEnd;

        @JsonProperty("businessStart")
        private LocalDate businessStart;

        @JsonProperty("businessEnd")
        private LocalDate businessEnd;
    }
}
