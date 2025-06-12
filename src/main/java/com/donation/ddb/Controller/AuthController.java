package com.donation.ddb.Controller;


import com.donation.ddb.Domain.AuthEvent;
import com.donation.ddb.Domain.CustomUserDetails;
import com.donation.ddb.Domain.Exception.DataNotFoundException;
import com.donation.ddb.Dto.Request.EmailVerificationRequestDto;
import com.donation.ddb.Dto.Request.WalletAddressVerifyRequestDto;
import com.donation.ddb.Dto.Request.WalletMessageRequestDTO;
import com.donation.ddb.Dto.Response.WalletAddressVerifyResponseDto;
import com.donation.ddb.Dto.Response.WalletMessageResponseDto;
import com.donation.ddb.Repository.AuthEventRepository;
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
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    @Autowired
    private final AuthService authService;
    private final AuthEventRepository authEventRepository;


    @PostMapping("/wallet/auth/request-message")
    public ResponseEntity<WalletMessageResponseDto> requestMessage(@RequestBody WalletMessageRequestDTO walletMessageRequestDTO){
        try {
            // AuthEvent 생성 (수정된 메서드명 사용)
            Long authEventId = authService.generateMessageByUserId(
                    walletMessageRequestDTO.getUserId(),
                    walletMessageRequestDTO.getWalletAddress()
            );

            // AuthEvent 조회
            Optional<AuthEvent> authEventOpt = authEventRepository.findById(authEventId);

            if (authEventOpt.isEmpty()) {
                throw new DataNotFoundException("생성된 AuthEvent를 찾을 수 없습니다.");
            }

            AuthEvent authEvent = authEventOpt.get();

            // 응답 DTO 생성
            WalletMessageResponseDto walletMessageResponseDto = new WalletMessageResponseDto();
            walletMessageResponseDto.setMessage(authEvent.getMessage()); // ✅ .getMessage() 메서드 호출 완료
            walletMessageResponseDto.setNonce(authEvent.getNonce());     // ✅ nonce도 추가
            walletMessageResponseDto.setAuthEventId(authEventId);

            return ResponseEntity.ok(walletMessageResponseDto);

        } catch (Exception e) {
            log.error("메시지 생성 중 오류 발생: {}", e.getMessage(), e);

            // 에러 응답 생성
            WalletMessageResponseDto errorResponse = new WalletMessageResponseDto();
            errorResponse.setMessage("메시지 생성 실패: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
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
