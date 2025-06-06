package com.donation.ddb.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class CampaignUpdateResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JoinResultDto {
        private Long id;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CampaignUpdateDto {
        private Long id;
        private String title;
        private String content;
        private String imageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public String getImageUrl() {
            if (imageUrl != null) {
                return imageUrl.replace("C:\\DDADDABLCH\\", "").replace("\\", "/");
            }
            return null; // Handle null case if needed
        }
    }
}
