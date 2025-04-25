package com.donation.ddb.Dto.Request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;


@Getter
@Setter
@NoArgsConstructor
public class StudentSignUpForm {

    @NotBlank(message="이름을 입력해주세요.")
    @JsonProperty("sName")
    private String sName;

    @NotBlank(message="닉네임을 입력해주세요.")
    @JsonProperty("sNickname")
    private String sNickname;

    @NotBlank(message="이메일을 입력해주세요.")
    @JsonProperty("sEmail")
    @Email(message="올바른 이메일 형식이어야 합니다.")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@sookmyung\\.ac\\.kr$",
            message = "숙명여대 이메일(@sookmyung.ac.kr)만 사용 가능합니다."
    )
    private String sEmail;

    @NotBlank(message="비밀번호를 입력해주세요")
    @JsonProperty("sPassword")
    private String sPassword;

    @NotBlank(message="비밀번호 확인을 입력해주세요.")
    @JsonProperty("sConfirmPassword")
    private String sConfirmPassword;
}
