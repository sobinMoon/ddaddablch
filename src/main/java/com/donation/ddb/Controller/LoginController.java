package com.donation.ddb.Controller;

import com.donation.ddb.Domain.CustomUserDetails;
import com.donation.ddb.Domain.RefreshToken;
import com.donation.ddb.Dto.Request.OrgLoginRequest;
import com.donation.ddb.Dto.Request.StudentLoginRequest;
import com.donation.ddb.Dto.Request.TokenRefreshRequestDto;
import com.donation.ddb.Dto.Response.TokenRefreshResponseDto;
import com.donation.ddb.Service.CustomUserDetailsService.CustomUserDetailsService;
import com.donation.ddb.Service.TokenService.JwtTokenProvider;
import com.donation.ddb.Service.TokenService.RefreshTokenService;
import com.donation.ddb.apiPayload.ApiResponse;
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
    public ResponseEntity<?> studentLogin(@Valid @RequestBody StudentLoginRequest loginRequestDto,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMap.put(error.getField(), error.getDefaultMessage());
                log.warn("로그인 유효성 검증 실패: {} - {}", error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(
                    ApiResponse.onFailure("VALIDATION_ERROR", "입력값이 올바르지 않습니다.", errorMap)
            );
        }

        // 인증
        log.info("학생 인증 시도: {}", loginRequestDto.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()
                    )
            );

            // 인증 성공 후 로그
            log.info("인증 성공. 인증 객체 클래스: {}", authentication.getClass().getName());
            log.info("Principal 클래스: {}", authentication.getPrincipal().getClass().getName());

            // CustomUserDetails로 캐스팅
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // 학생 사용자인지 확인
            if (!userDetails.isStudent()) {
                log.warn("학생 로그인 시도했지만 단체 계정입니다: {}", loginRequestDto.getEmail());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "success", false,
                                "message", "학생 계정이 아닙니다. 단체 로그인을 이용해주세요."
                        ));
            }

            // SecurityContext에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Access Token 생성
            String jwt = jwtTokenProvider.generateToken(authentication);

            // Refresh Token 생성 및 저장
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authentication.getName());

            // 응답 데이터 구성
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "로그인 성공");
            responseData.put("token", jwt);
            responseData.put("refreshToken", refreshToken.getToken());

            // 사용자 정보 추가
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", userDetails.getId());
            userInfo.put("email", userDetails.getEmail());
            userInfo.put("role", userDetails.getRole());
            userInfo.put("userType", "STUDENT");
            //responseData.put("user", userInfo);

            log.info("학생 로그인 성공: ID={}, Email={}", userDetails.getId(), userDetails.getEmail());

            return ResponseEntity.ok(responseData);

        } catch (AuthenticationException e) {
            // 비밀번호 또는 이메일 불일치로 오류 발생
            log.warn("학생 로그인 인증 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", "이메일 또는 비밀번호가 일치하지 않습니다."
                    ));
        } catch (Exception e) {
            log.error("학생 인증 실패 예외: {} - {}", e.getClass().getName(), e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "서버 에러입니다."
                    ));
        }
    }
    /*@PostMapping("/student")
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

    }*/
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
    public ResponseEntity<?> login(@Valid @RequestBody OrgLoginRequest loginRequestDto,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errorMap.put(error.getField(), error.getDefaultMessage());
                log.warn("로그인 유효성 검증 실패: {} - {}", error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(
                    ApiResponse.onFailure("VALIDATION_ERROR", "입력값이 올바르지 않습니다.", errorMap)
            );
        }

        // 인증
        log.info("인증 시도: {}", loginRequestDto.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()
                    )
            );

            // 인증 성공 후 로그
            log.info("인증 성공. 인증 객체 클래스: {}", authentication.getClass().getName());
            log.info("Principal 클래스: {}", authentication.getPrincipal().getClass().getName());

            // CustomUserDetails로 캐스팅
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // 단체 사용자인지 확인
            if (!userDetails.isOrganization()) {
                log.warn("단체 로그인 시도했지만 학생 계정입니다: {}", loginRequestDto.getEmail());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "success", false,
                                "message", "단체 계정이 아닙니다. 학생 로그인을 이용해주세요."
                        ));
            }

            // SecurityContext에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Access Token 생성
            String jwt = jwtTokenProvider.generateToken(authentication);

            // Refresh Token 생성 및 저장
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authentication.getName());

            // 응답 데이터 구성
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "로그인 성공");
            responseData.put("token", jwt);
            responseData.put("refreshToken", refreshToken.getToken());

            // 사용자 정보 추가
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", userDetails.getId());
            userInfo.put("email", userDetails.getEmail());
            userInfo.put("role", userDetails.getRole());
            userInfo.put("userType", "ORGANIZATION");
            //responseData.put("user", userInfo);

            log.info("단체 로그인 성공: ID={}, Email={}", userDetails.getId(), userDetails.getEmail());

            return ResponseEntity.ok(responseData);

        } catch (AuthenticationException e) {
            // 비밀번호 또는 이메일 불일치로 오류 발생
            log.warn("로그인 인증 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", "이메일 또는 비밀번호가 일치하지 않습니다."
                    ));
        } catch (Exception e) {
            log.error("인증 실패 예외: {} - {}", e.getClass().getName(), e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "서버 에러입니다."
                    ));
        }
    }
}
