package com.donation.ddb.Dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
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
    @JsonProperty("name")
    private String sName;

    //StudentUser 생성 시점에 @PrePersist로 닉네임 설정하기
    @JsonProperty("nickname")
    private String sNickname;

    @NotBlank(message="이메일을 입력해주세요.")
    @JsonProperty("email")
    @Email(message="올바른 이메일 형식이어야 합니다.")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@sookmyung\\.ac\\.kr$",
            message = "숙명여대 이메일(@sookmyung.ac.kr)만 사용 가능합니다."
    )
    private String sEmail;

    //비밀번호 복잡성 검증 로직 필요하면 추가하기
    @NotBlank(message="비밀번호를 입력해주세요")
    @JsonProperty("password")
    private String sPassword;

    @NotBlank(message="비밀번호 확인을 입력해주세요.")
    @JsonProperty("confirmPassword")
    private String sConfirmPassword;

}
