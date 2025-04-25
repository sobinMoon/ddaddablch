package com.donation.ddb.Dto.Response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WalletAddressVerifyResponseDto {
  private boolean success;
  //private String message;

//  public static WalletAddressVerifyResponseDto of(boolean success,String message){
//      WalletAddressVerifyResponseDto dto=new WalletAddressVerifyResponseDto();
//      dto.setSuccess(success);
//      dto.setMessage(message);
//      return dto;
//  }
}
