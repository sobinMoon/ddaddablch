package com.donation.ddb.Controller;


import com.donation.ddb.Service.JwtTokenProvider;
import com.donation.ddb.Service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// LogoutController.java 생성
@RestController
@RequestMapping("/auth/logout")
@RequiredArgsConstructor
@Slf4j
public class LogoutController {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    /*
    @PostMapping("/")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // 헤더에서 JWT 토큰 추출
        String token = jwtTokenProvider.resolveToken(request);
        log.info("헤더에서 jwt 토큰 추출 완료 ");
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 클레임에서 사용자명 추출
            String username = jwtTokenProvider.getUsernameFromToken(token);
            log.info("사용자명 추출완료 ");
            // 해당 사용자의 RefreshToken 삭제
            refreshTokenService.deleteByUsername(username);
            log.info("토큰 삭제 완료 " +
                    " ");
            log.info("사용자 {} 로그아웃 성공", username);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "로그아웃 성공"
                    )
            );
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of(
                        "success", false,
                        "message", "유효하지 않은 토큰"
                )
        );
    }*/
}
