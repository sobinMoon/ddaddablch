package com.donation.ddb.Controller;


import com.donation.ddb.Domain.DataNotFoundException;
import com.donation.ddb.Dto.Request.DuplicateNicknameRequestDto;
import com.donation.ddb.Dto.Request.EmailVerificationRequestDto;
import com.donation.ddb.Dto.Request.OrgSignUpForm;
import com.donation.ddb.Dto.Request.StudentSignUpForm;
import com.donation.ddb.Dto.Response.DuplicateNicknameResponseDto;
import com.donation.ddb.Service.EmailService;
import com.donation.ddb.Service.JwtTokenProvider;
import com.donation.ddb.Service.OrgUserService;
import com.donation.ddb.Service.StudentUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.naming.Binding;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.donation.ddb.Domain.Role.ROLE_STUDENT;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/sign-up")
@Slf4j //로깅
public class StudentUserController {

    @Autowired
    private final StudentUserService studentUserService;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final OrgUserService orgUserService;


    @GetMapping("/duplicate-check")
    public ResponseEntity<?> duplicatenickname(
            @RequestParam(name="nickname") String user_nickname
            ){

        if(user_nickname==null || user_nickname.trim().isEmpty()){
            Map<String,Object> errorResponse=new HashMap<>();
            errorResponse.put("success",false);
            errorResponse.put("message","닉네임을 입력해주세요");
            //닉네임 비어있으면 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        Boolean isDuplicate=(studentUserService.duplicateNickname(user_nickname));

        DuplicateNicknameResponseDto response=new DuplicateNicknameResponseDto();
        if(!isDuplicate)//중복아님
        {
        response.setDuplicate(false);
        }else{//중복임
        response.setDuplicate(true);
        }

        //200 OK 상태코드사용
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/student")
    public ResponseEntity<?> signup(@RequestBody @Valid StudentSignUpForm studentSignUpForm,
                                   BindingResult bindingResult){

        //로깅
        log.info("회원가입 요청 수신: ",studentSignUpForm.getSEmail());

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
            Long userId=studentUserService.createUser(
                    studentSignUpForm.getSName(),
                    studentSignUpForm.getSNickname(),
                    studentSignUpForm.getSEmail(),
                    studentSignUpForm.getSPassword(),
                    studentSignUpForm.getSConfirmPassword()
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

            return ResponseEntity.status(HttpStatus.CREATED).body(
              Map.of("success",true,
                      "message","학생 회원가입이 완료됐습니다.")
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
    @PostMapping("/send-verification-email")
    public ResponseEntity<?> sendVerificationEmail(
            @Valid @RequestBody EmailVerificationRequestDto request, BindingResult bindingResult){
        if(bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                String fieldName = error.getField();
                String errorMessage = error.getDefaultMessage();
                errorMap.put(fieldName, errorMessage);
                log.warn("회원가입 유효성 검증 실패 : {} - {}", fieldName, errorMessage
                );
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        }
        try {
            emailService.sendVerificationEmail(request.getEmail());
            return ResponseEntity.ok(
                    Map.of("success", true,
                            "message", "인증 메일이 전송됐습니다.")
            );
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    //Map.of("error",e.getMessage())

                    Map.of("success", false,
                            "message", "인증 메일 전송 실패했습니다."
                    )
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





}
