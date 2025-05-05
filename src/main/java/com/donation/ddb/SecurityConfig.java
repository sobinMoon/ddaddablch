package com.donation.ddb;

import com.donation.ddb.Service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
                //세션 사용 안함
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                //iframe 보안 설정
                .headers((headers) -> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))

                //
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**","/api/**","/api/v1/user/sign-up/**" , "/wallet/**","/h2-console/**").permitAll()  // 회원가입, 로그인, 인증 경로는 허용
                        .anyRequest().authenticated()             // 그 외는 인증 필요
                )

                //JWT 인증 필터 등록
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                       UsernamePasswordAuthenticationFilter.class);


                //JWT 인증 필터를 Spring Security의 기본 인증 필터 이전에 추가함.
                //요청 헤더에서 JWT 토큰 추출하고 검증한 후, 인증 정보를 SecurityContext에 설정
                //다른 Spring Security 필터들이 JWT 토큰으로 인증된 사용자 정보 사용할 수 있음.
        return http.build();
    }

    // CORS 설정을 위한 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 오리진(출처) 설정
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",     // React 기본 포트
                "http://localhost:5500",     // Live Server 기본 포트
                "http://localhost:8000",     // Python 서버 기본 포트
                "http://127.0.0.1:5500",
                "http://127.0.0.1:8000"
        ));

        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 허용할 헤더 설정
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

        // 자격 증명(쿠키 등) 허용 설정
        configuration.setAllowCredentials(true);

        // 브라우저가 접근할 수 있는 헤더 설정
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        // preflight 요청의 캐시 시간 설정 (초 단위)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 CORS 설정 적용

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}