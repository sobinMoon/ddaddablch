package com.donation.ddb.Controller;


import com.donation.ddb.Dto.Request.WalletAddressVerifyRequestDto;
import com.donation.ddb.Dto.Request.WalletMessageRequestDTO;
import com.donation.ddb.Dto.Response.WalletAddressVerifyResponseDto;
import com.donation.ddb.Dto.Response.WalletMessageResponseDto;
import com.donation.ddb.Service.WalletService.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/wallet/auth/")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    @Autowired
    private final AuthService authService;

    @PostMapping("/request-message")
    public ResponseEntity<WalletMessageResponseDto> requestMessage(@RequestBody WalletMessageRequestDTO walletMessageRequestDTO){

        String message=authService.generateMessage(walletMessageRequestDTO.getEmail(),walletMessageRequestDTO.getWalletAddress());
        WalletMessageResponseDto walletMessageResponseDto=new WalletMessageResponseDto();
        walletMessageResponseDto.setMessage(message);
        return ResponseEntity.ok(walletMessageResponseDto);
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
