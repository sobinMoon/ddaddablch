package com.donation.ddb.Dto.Response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WalletMessageResponseDto {
    private String message;
    private String nonce;
    private Long authEventId;  // AuthEvent ID 추가

    public static WalletMessageResponseDto of(String message, String nonce, Long authEventId) {
        WalletMessageResponseDto dto = new WalletMessageResponseDto();
        dto.setMessage(message);
        dto.setNonce(nonce);
        dto.setAuthEventId(authEventId);
        return dto;
    }
}