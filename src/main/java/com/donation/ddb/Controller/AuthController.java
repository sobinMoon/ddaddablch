package com.donation.ddb.Controller;


import com.donation.ddb.Dto.Request.WalletAddressVerifyRequestDto;
import com.donation.ddb.Dto.Request.WalletNonceRequestDTO;
import com.donation.ddb.Dto.Response.WalletAddressVerifyResponseDto;
import com.donation.ddb.Dto.Response.WalletNonceResponseDto;
import com.donation.ddb.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/request-nonce")
    public ResponseEntity<WalletNonceResponseDto> requestNonce(@RequestBody WalletNonceRequestDTO walletNonceRequestDTO){

        String nonce=authService.generateNonce(walletNonceRequestDTO.getEmail(),walletNonceRequestDTO.getWalletAddress());

        WalletNonceResponseDto walletNonceResponseDto=new WalletNonceResponseDto();
        walletNonceResponseDto.setNonce(nonce);
        return ResponseEntity.ok(walletNonceResponseDto);
    }

    @PostMapping("/verify-signature")
    public ResponseEntity<WalletAddressVerifyResponseDto> verify(@RequestBody @Valid WalletAddressVerifyRequestDto walletAddressVerifyDto,
                                                                BindingResult bindingResult) {
        boolean isValid = authService.verifySignature(walletAddressVerifyDto);

        WalletAddressVerifyResponseDto response = new WalletAddressVerifyResponseDto();
        if (isValid) {
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } else {
            response.setSuccess(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
