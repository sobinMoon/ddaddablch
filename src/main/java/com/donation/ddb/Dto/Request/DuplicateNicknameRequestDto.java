package com.donation.ddb.Dto.Request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DuplicateNicknameRequestDto {
    @NotBlank
    String nickname;
}
