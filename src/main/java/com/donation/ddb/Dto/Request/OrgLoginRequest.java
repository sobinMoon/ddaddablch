package com.donation.ddb.Dto.Request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgLoginRequest {

    @NotBlank(message="이메일을 입력해주세요")
    @Email(message="유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message="비밀번호를 입력해주세요")
    private String password;

}
