package com.donation.ddb.Dto.Request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class OrgInfoUpdateRequestDTO {
    // 조직 설명 수정
    @Size(max = 500, message = "조직 설명은 500자 이하여야 합니다.")
    private String description;

    // 현재 비밀번호 (확인용)
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;
}
