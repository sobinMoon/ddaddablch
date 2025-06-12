package com.donation.ddb.Service.TokenService;

import com.donation.ddb.Domain.CustomUserDetails;
import com.donation.ddb.Domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jnr.constants.platform.Local;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration}")
    private int refreshTokenExpirationMs;

    //인증된 사용자 정보를 기반으로 jwt생성
    //ACCESS TOKEN생성
    public String generateToken(Authentication authentication){
        // 안전한 캐스팅 처리
        CustomUserDetails customUserDetails;
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        } else {
            // 캐스팅 불가능한 경우 대체 로직
            throw new RuntimeException("인증 객체에서 UserDetails를 찾을 수 없습니다.");
        }

        Date now = new Date();
        Date expiryDate=new Date(now.getTime()+jwtExpirationMs);

        // 권한 뽑기
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
//        List<String> roles = authorities.stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(customUserDetails.getUsername())
                .claim("userId", customUserDetails.getId())   // ← 추가
                .claim("role", customUserDetails.getRole())   // ← 추가
                .claim("nickname",customUserDetails.getNickname())
                //.claim("authorities",customUserDetails.getAuthorities())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS512)
                .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken(Authentication authentication) {
        String username = authentication.getName();//여기서 name은 email임

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS512)
                .compact();
    }

    // Request에서 Authorization 헤더로 토큰 꺼내기
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰에서 인증 정보 꺼내기
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.getSubject();
        Long userId = claims.get("userId", Long.class);
        String role = claims.get("role", String.class);
        String nickname=claims.get("nickname",String.class);


        // null 값들을 안전하게 처리
        CustomUserDetails customUserDetails = CustomUserDetails.builder()
                .id(userId != null ? userId : 0L)
                .email(email != null ? email : "")
                .nickname(nickname != null ? nickname : "") // null 방지
                .password("")
                .role(role != null ? role : "")
                .build();

        // Spring Security에 등록할 인증 객체 반환
        return new UsernamePasswordAuthenticationToken(customUserDetails, "",
                customUserDetails.getAuthorities() );
    }

    // 토큰에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}




