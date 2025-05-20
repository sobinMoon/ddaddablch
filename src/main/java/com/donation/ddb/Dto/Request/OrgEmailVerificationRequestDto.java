package com.donation.ddb.Dto.Request;


import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrgEmailVerificationRequestDto {
    @Email(message="유효한 이메일 형식이 아닙니다.")
    private String email;
}
