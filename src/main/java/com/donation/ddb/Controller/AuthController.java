package com.donation.ddb.Controller;


import com.donation.ddb.Dto.Request.EmailVerificationRequestDto;
import com.donation.ddb.Dto.Request.WalletAddressVerifyRequestDto;
import com.donation.ddb.Dto.Request.WalletNonceRequestDTO;
import com.donation.ddb.Dto.Response.WalletAddressVerifyResponseDto;
import com.donation.ddb.Dto.Response.WalletNonceResponseDto;
import com.donation.ddb.Service.AuthService;
import com.donation.ddb.Service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthService authService;
    private final EmailService emailService;

    @PostMapping("/request-nonce")
    public ResponseEntity<WalletNonceResponseDto> requestNonce(@RequestBody WalletNonceRequestDTO walletNonceRequestDTO){

        String message=authService.generateNonce(walletNonceRequestDTO.getEmail(),walletNonceRequestDTO.getWalletAddress());

        WalletNonceResponseDto walletNonceResponseDto=new WalletNonceResponseDto();
        walletNonceResponseDto.setMessage(message);
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

    @PostMapping("/send-verification-email")
    public ResponseEntity<?> sendVerificationEmail(
            @Valid @RequestBody EmailVerificationRequestDto request){
        try{
            emailService.sendVerificationEmail(request.getEmail());
            return ResponseEntity.ok(
                    Map.of("success",true,
                            "message","인증 메일이 전송됐습니다.")
            );
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    //Map.of("error",e.getMessage())
                    Map.of("success",false,
                            "message","인증 메일 전송 실패했습니다.")
            );
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token ){
        boolean verified=emailService.verifyEmail(token);

        if(verified){
            return ResponseEntity.status(HttpStatus.OK).body(
                    Map.of("success",true,
                            "message","인증에 성공했습니다.")
            );
        }else{
            Map<String,Object> errorResponse=new HashMap<>();
            errorResponse.put("success",false);
            errorResponse.put("message","인증에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                   errorResponse
            );
        }
    }

}
