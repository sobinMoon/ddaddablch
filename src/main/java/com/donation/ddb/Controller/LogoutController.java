package com.donation.ddb.Controller;


import com.donation.ddb.Dto.Request.LogoutRequest;
import com.donation.ddb.Repository.RefreshTokenRepository;
import com.donation.ddb.Service.AuthService;
import com.donation.ddb.Service.JwtTokenProvider;
import com.donation.ddb.Service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// LogoutController.java 생성
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class LogoutController {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthService authService;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest logoutRequest) {

        // 리프레시 토큰으로 로그아웃 처리
        String refreshToken = logoutRequest.getRefreshToken();
        try {
            authService.deleteToken(refreshToken);

            return ResponseEntity.status(HttpStatus.OK).body(
                    Map.of("success", true,
                            "message", "로그아웃 성공")
            );
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("success", false,
                            "message", "로그아웃 실패 - 서버 오류")
            );
        }
    }
}
