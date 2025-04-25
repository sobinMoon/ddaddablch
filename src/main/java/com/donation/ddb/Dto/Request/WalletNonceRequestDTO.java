package com.donation.ddb.Dto.Request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WalletNonceRequestDTO {

 private String email;
 private String walletAddress;
}
