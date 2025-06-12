package com.donation.ddb.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
public class StudentUserResponse {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentUserCommentDto {
        private Long id;
        private String nickname;
        private String profileImage;
        private String role;
        private Boolean isActive;

        public static StudentUserCommentDto from(com.donation.ddb.Domain.StudentUser studentUser) {
            return StudentUserCommentDto.builder()
                    .id(studentUser.getSId())
                    .nickname(studentUser.getSNickname())
                    .profileImage(studentUser.getSProfileImage())
                    .role(String.valueOf(studentUser.getRole()))
                    .isActive(studentUser.getSIsActive())
                    .build();
        }
    }
}
