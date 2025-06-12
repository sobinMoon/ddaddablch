package com.donation.ddb.Dto.Request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class OrgInfoUpdatePwdDTO {

    // 현재 비밀번호 (확인용)
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    // 새 비밀번호
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    //@Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    private String newPassword;

    // 새 비밀번호 확인
    @NotBlank(message = "새 비밀번호 확인을 입력해주세요.")
    private String confirmNewPassword;
}
