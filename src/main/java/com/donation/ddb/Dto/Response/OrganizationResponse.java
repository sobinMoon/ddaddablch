package com.donation.ddb.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OrganizationResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganizationDetailDto {
        private Long oId;
        private String oName;
        private String oProfileImage;
        private String oDescription;
    }
}
