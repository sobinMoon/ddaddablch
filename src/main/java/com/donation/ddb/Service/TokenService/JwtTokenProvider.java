package com.donation.ddb.Service.TokenService;

import com.donation.ddb.Domain.CustomUserDetails;
import com.donation.ddb.Domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


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
        UserDetails userDetails;
        if (authentication.getPrincipal() instanceof UserDetails) {
            userDetails = (UserDetails) authentication.getPrincipal();
        } else {
            // 캐스팅 불가능한 경우 대체 로직
            throw new RuntimeException("인증 객체에서 UserDetails를 찾을 수 없습니다.");
        }

        Date now = new Date();
        Date expiryDate=new Date(now.getTime()+jwtExpirationMs);

        // 권한 뽑기
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles",roles)
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

        String username = claims.getSubject();

        // JWT에 저장된 roles 클레임에서 권한 정보 꺼내기
        List<String> roles = claims.get("roles", List.class);

        // 권한 문자열을 Spring Security에서 사용하는 형식으로 변환
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 인증된 사용자 객체 생성 (비밀번호는 필요 없으니 공란으로)
//        UserDetails userDetails = org.springframework.security.core.userdetails.User
//                .withUsername(username)
//                .password("")
//                .authorities(authorities)
//                .build();

        CustomUserDetails customUserDetails = CustomUserDetails.builder()
                .email(username)
                .password("")
                .authorities(authorities)
                .role(roles.contains(Role.ROLE_STUDENT.name()) ? Role.ROLE_STUDENT.name() : Role.ROLE_ORGANIZATION.name())
                .build();

        // Spring Security에 등록할 인증 객체 반환
        return new UsernamePasswordAuthenticationToken(customUserDetails, "", authorities);
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



