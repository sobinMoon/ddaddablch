package com.donation.ddb.Dto.Request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WalletAddressVerifyRequestDto {

    @NotNull(message = "인증 이벤트 ID를 입력해주세요.")
    private Long authEventId;  // AuthEvent의 ID로 식별

    @NotBlank
    private String walletAddress;

    @NotBlank
    private String message;

    @NotBlank
    private String signature;
}
