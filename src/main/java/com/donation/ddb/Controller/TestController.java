package com.donation.ddb.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/api/test/protected")
    public ResponseEntity<?> protectedEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // "anonymousUser"인지 확인
        if ("anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "인증되지 않은 사용자입니다.",
                    "authenticated", false
            ));
        }

        // 인증된 사용자일 경우
        return ResponseEntity.ok(Map.of(
                "message", "보호된 자원에 접근 성공!",
                "user", authentication.getName(),
                "authenticated", true
        ));
    }
}
