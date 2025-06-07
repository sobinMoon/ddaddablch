package com.donation.ddb.Dto.Request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class StudentInfoUpdatePwdDTO {
    // 비밀번호 수정 (현재 비밀번호 확인용)
    private String currentPassword;

    // 새 비밀번호
    private String newPassword;

    // 새 비밀번호 확인
    private String confirmNewPassword;
}
