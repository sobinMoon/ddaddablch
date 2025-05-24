package com.donation.ddb.Converter;

import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Response.StudentUserResponse;

public class StudentUserConverter {
    public static StudentUserResponse.StudentUserCommentDto toCommentDto(StudentUser studentUser) {
        return StudentUserResponse.StudentUserCommentDto.builder()
                .id(studentUser.getSId())
                .nickname(studentUser.getSNickname())
                .profileImage(studentUser.getSProfileImage())
                .role(String.valueOf(studentUser.getRole()))
                .isActive(studentUser.getSIsActive())
                .build();
    }
}
