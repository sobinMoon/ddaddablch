package com.donation.ddb.Controller;


import com.donation.ddb.Domain.DataNotFoundException;
import com.donation.ddb.Dto.Request.EmailVerificationRequestDto;
import com.donation.ddb.Dto.Request.OrgEmailVerificationRequestDto;
import com.donation.ddb.Dto.Request.OrgSignUpForm;
import com.donation.ddb.Repository.OrganizationUserRepository;
import com.donation.ddb.Service.EmailService;
import com.donation.ddb.Service.JwtTokenProvider;
import com.donation.ddb.Service.OrgUserService;
import com.donation.ddb.Service.StudentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/org")
@Slf4j //로깅
public class OrgUserController {

    @Autowired
    private final OrgUserService orgUserService;
    private final OrganizationUserRepository organizationUserRepository;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/send-verification-email")
    public ResponseEntity<?> sendVerificationEmail(
            @Valid @RequestBody OrgEmailVerificationRequestDto request,
            BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                String fieldName = error.getField();
                String errorMessage = error.getDefaultMessage();
                errorMap.put(fieldName, errorMessage);
                log.warn("회원가입 유효성 검증 실패 : {} - {}", fieldName, errorMessage);
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        }

        try {
            log.info("이메일 인증 요청: {}", request.getEmail());
            emailService.sendVerificationEmail(request.getEmail());
            log.info("이메일 인증 성공: {}", request.getEmail());
            return ResponseEntity.ok(
                    Map.of("success", true,
                            "message", "인증 메일이 전송됐습니다.")
            );
        } catch (Exception e) {
            // 예외의 전체 스택 트레이스를 로그에 기록
            log.error("이메일 전송 실패: {} - {}", request.getEmail(), e.getMessage(), e);

            // 원인 예외(cause)가 있다면 함께 로그에 기록
            Throwable cause = e.getCause();
            if (cause != null) {
                log.error("원인 예외: {}", cause.getMessage(), cause);
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("success", false,
                            "message", "인증 메일 전송에 실패했습니다. 관리자에게 문의해주세요.")
            );
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam(value="token") String token){
        boolean verified=emailService.verifyEmail(token);

        if(verified){
            return ResponseEntity.status(HttpStatus.OK).body(
                    Map.of("success",true,
                            "message","인증에 성공했습니다.")
            );
        }else{
            Map<String,Object> errorResponse=new HashMap<>();
            errorResponse.put("success",false);
            errorResponse.put("message","인증에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    errorResponse
            );
        }
    }

    @PostMapping("/sign-up/organization")
    public ResponseEntity<?> signup(@RequestBody @Valid OrgSignUpForm orgSignUpForm,
                                    BindingResult bindingResult){

        //로깅
        log.info("단체 회원가입 요청 수신: ", orgSignUpForm.getEmail());

        //유효성 검증 실패했을 때 처리
        if(bindingResult.hasErrors()){
            Map<String, String> errorMap=new HashMap<>();
            bindingResult.getFieldErrors().forEach(error->{
                String fieldName=error.getField();
                String errorMessage=error.getDefaultMessage();
                errorMap.put(fieldName,errorMessage);
                log.warn("회원가입 유효성 검증 실패 : {} - {}",fieldName, errorMessage
                );
            });
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(
                            Map.of("success","false",
                                    "message","회원가입 유효성 검증 싈패 ")
                    );
        }

        try {
            Long userId=orgUserService.createOrg(
                    orgSignUpForm.getName(),
                    orgSignUpForm.getEmail(),
                    orgSignUpForm.getPassword(),
                    orgSignUpForm.getConfirmPassword(),
                    orgSignUpForm.getBusinessNumber(),
                    orgSignUpForm.getWalletAddress(),
                    orgSignUpForm.getDescription(),
                    orgSignUpForm.getProfileImage()
            );

            log.info("회원가입 성공 : 사용자 ID {} ",userId);


//            //  권한 설정 (기본적으로 ROLE_USER 또는 ROLE_STUDENT 등)
//            List<GrantedAuthority> authorities = List.of(
//                    new SimpleGrantedAuthority(ROLE_STUDENT.name()));
//
//
//            //Authentication 객체 생성
//            Authentication auth=new UsernamePasswordAuthenticationToken(
//                    new org.springframework.security.core.userdetails.User(
//                            studentSignUpForm.getSEmail(), "", authorities
//                    ),
//                    null,
//                    authorities
//            );
//
//
//            //JWT 토큰 생성
//            String jwt=jwtTokenProvider.generateToken(auth);
            //log.info("==> 저장 직전 oWalletAddress: " + organizationUserRepository.findByoWalletAddress());
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("success",true,
                            "message","단체 회원가입이 완료됐습니다.")
            );
        }catch(IllegalArgumentException | IllegalStateException e){
            log.error("회원가입 처리 중 오류 발생 ",e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch(DataNotFoundException e){
            log.error("회원가입 처리 중 데이터 관련 오류 : {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }catch(Exception e){
            log.error("회원가입 처리 중 오류 : {}",e.getMessage(),e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류 발생");
        }
    }

}
