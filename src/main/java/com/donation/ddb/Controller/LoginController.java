package com.donation.ddb.Controller;

import com.donation.ddb.Domain.RefreshToken;
import com.donation.ddb.Dto.Request.OrgLoginRequest;
import com.donation.ddb.Dto.Request.StudentLoginRequestDto;
import com.donation.ddb.Dto.Request.TokenRefreshRequestDto;
import com.donation.ddb.Dto.Response.TokenRefreshResponseDto;
import com.donation.ddb.Service.CustomUserDetailsService;
import com.donation.ddb.Service.JwtTokenProvider;
import com.donation.ddb.Service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/student")
    public ResponseEntity<?> login(@Valid @RequestBody
                                       StudentLoginRequestDto loginRequestDto,
                                   BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            Map<String,String> errorMap=new HashMap<>();
            bindingResult.getFieldErrors().forEach(error->{
                errorMap.put(error.getField(),error.getDefaultMessage());
                log.warn("로그인 유효성 검증 실패: {} -{} ",error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errorMap);
        }

        //인증

        log.info("인증 시도: " + loginRequestDto.getEmail());

        try{
            Authentication authentication=
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequestDto.getEmail(),
                                    loginRequestDto.getPassword()
                            )
                    );
            // 인증 성공 후 로그
            log.info("인증 성공. 인증 객체 클래스: " + authentication.getClass().getName());
            log.info("Principal 클래스: " + authentication.getPrincipal().getClass().getName());


            SecurityContextHolder.getContext().setAuthentication(authentication);

            //Access Token 생성
            String jwt=jwtTokenProvider.generateToken(authentication);

            //Refresh Token 생성 및 저장
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authentication.getName());

            return ResponseEntity.ok(
                    Map.of(
                            "success",true,
                            "message","로그인 성공",
                            "token",jwt,
                            "refreshToken", refreshToken.getToken()
                    )
            );




        } catch(AuthenticationException e){
            //비밀번호 또는 이메일 불일치로 오류 발생
            log.warn("로그인 인증 실패 : ; + {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of("success",false,
                                    "message","이메일 또는 비밀번호가 일치하지 않습니다.")
                    );
        } catch(Exception e){
            System.out.println("인증 실패 예외: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of("success",false,
                                    "message","서버 오류가 발생했습니다.")
                    );
        }

    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequestDto request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUsername)
                .map(username -> {
                    // 사용자 정보로 UserDetails 생성
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    // 새 Access Token 생성
                    String accessToken = jwtTokenProvider.generateToken(authentication);

                    return ResponseEntity.ok(new TokenRefreshResponseDto(accessToken, requestRefreshToken));
                })
                .orElseThrow(() -> new RuntimeException("Refresh 토큰이 db에 없습니다."));
    }

    @PostMapping("/org")
    public ResponseEntity<?> login(@Valid @RequestBody
                                       OrgLoginRequest loginRequestDto,
                                   BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            Map<String,String> errorMap=new HashMap<>();
            bindingResult.getFieldErrors().forEach(error->{
                errorMap.put(error.getField(),error.getDefaultMessage());
                log.warn("로그인 유효성 검증 실패: {} -{} ",error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errorMap);
        }

        //인증
        log.info("인증 시도: " + loginRequestDto.getEmail());

        try{
            Authentication authentication=
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequestDto.getEmail(),
                                    loginRequestDto.getPassword()
                            )
                    );
            // 인증 성공 후 로그
            log.info("인증 성공. 인증 객체 클래스: " + authentication.getClass().getName());
            log.info("Principal 클래스: " + authentication.getPrincipal().getClass().getName());


            SecurityContextHolder.getContext().setAuthentication(authentication);

            //Access Token 생성
            String jwt=jwtTokenProvider.generateToken(authentication);

            //Refresh Token 생성 및 저장
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authentication.getName());

            return ResponseEntity.ok(
                    Map.of(
                            "success",true,
                            "message","로그인 성공",
                            "token",jwt,
                            "refreshToken", refreshToken.getToken()
                    )
            );
        } catch(AuthenticationException e){
            //비밀번호 또는 이메일 불일치로 오류 발생
            log.warn("로그인 인증 실패 : ; + {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of("success",false,
                                    "message","이메일 또는 비밀번호가 일치하지 않습니다.")
                    );
        } catch(Exception e){
            System.out.println("인증 실패 예외: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of("success",false,
                                    "message","서버 에러입니다.")
                    );
        }

    }*/
}
