package com.donation.ddb.Controller;


import com.donation.ddb.Domain.CustomUserDetails;
import com.donation.ddb.Dto.Request.EmailVerificationRequestDto;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    @Autowired
    private final AuthService authService;

    @PostMapping("/wallet/auth/request-message")
    public ResponseEntity<WalletMessageResponseDto> requestMessage(@RequestBody WalletMessageRequestDTO walletMessageRequestDTO){

        String message=authService.generateMessage(walletMessageRequestDTO.getEmail(),walletMessageRequestDTO.getWalletAddress());
        WalletMessageResponseDto walletMessageResponseDto=new WalletMessageResponseDto();
        walletMessageResponseDto.setMessage(message);
        return ResponseEntity.ok(walletMessageResponseDto);
    }


    @PostMapping("/wallet/auth/verify-signature")
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
//    @GetMapping("/auth/me")
//    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails){
//        if(userDetails==null){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("authenticated", false,
//                            "error", "인증되지 않은 사용자입니다."));
//        }
//        Map<String, Object> userInfo = Map.of(
//                "id", userDetails.getId(),
//                "email", userDetails.getEmail(),
//                "role", userDetails.getRole(),
//                "nickname", userDetails.getNickname()
//        );
//
//        return ResponseEntity.ok(userInfo);
//
//    }
@GetMapping("/auth/me")
public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails){
    if(userDetails == null){
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("authenticated", false);
        errorResponse.put("error", "인증되지 않은 사용자입니다.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // 디버깅용 로그
    System.out.println("=== 사용자 정보 디버깅 ===");
    System.out.println("ID: " + userDetails.getId() + " (null: " + (userDetails.getId() == null) + ")");
    System.out.println("Email: " + userDetails.getEmail() + " (null: " + (userDetails.getEmail() == null) + ")");
    System.out.println("Role: " + userDetails.getRole() + " (null: " + (userDetails.getRole() == null) + ")");
    System.out.println("Nickname: " + userDetails.getNickname() + " (null: " + (userDetails.getNickname() == null) + ")");

    Map<String, Object> userInfo = new HashMap<>();
    userInfo.put("id", userDetails.getId());
    userInfo.put("email", userDetails.getEmail());
    userInfo.put("role", userDetails.getRole());
    userInfo.put("nickname", userDetails.getNickname());

    return ResponseEntity.ok(userInfo);
}



}
