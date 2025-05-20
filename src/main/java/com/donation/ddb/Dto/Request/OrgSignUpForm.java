package com.donation.ddb.Dto.Request;

import com.donation.ddb.validation.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@PasswordMatches
public class OrgSignUpForm {

    @NotBlank(message="이메일을 입력해주세요")
    @Email(message="올바른 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message="단체명을 입력해주세요")
    private String name;

    @NotBlank(message="비밀번호를 입력해주세요")
    private String password;

    @NotBlank(message="비밀번호를 입력해주세요")
    private String confirmPassword;

    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "올바른 지갑 주소 형식이어야 합니다.")
    private String walletAddress;

    private String description;

    private String profileImage;



    @Pattern(regexp = "\\d{3}-\\d{2}-\\d{5}", message = "사업자등록번호는 123-45-67890 형식이어야 합니다.")
    @NotBlank(message="사업자 번호를 입력해주세요")
    private String businessNumber;



}
