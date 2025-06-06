package com.donation.ddb.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WalletMessageRequestDTO {
 @NotBlank
 private Long userId;
 @NotBlank
 private String walletAddress;
}
