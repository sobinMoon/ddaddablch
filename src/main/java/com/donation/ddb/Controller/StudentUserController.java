package com.donation.ddb.Controller;


import com.donation.ddb.Dto.Request.StudentSignUpForm;
import com.donation.ddb.Repository.StudentUserRepository;
import com.donation.ddb.Service.StudentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class StudentUserController {

    @Autowired
    private StudentUserService studentUserService;

    @PostMapping("/user/sign-up/student")
    public ResponseEntity<?> signup(@RequestBody @Valid StudentSignUpForm studentSignUpForm,
                                   BindingResult bindingResult){
        System.out.println("받은 비밀번호: " + studentSignUpForm.getSPassword());

        if(bindingResult.hasErrors()){

            Map<String, String> errorMap=new HashMap<>();

            bindingResult.getFieldErrors().forEach(error->{
                String fieldName=error.getField();
                String errorMessage=error.getDefaultMessage();
                errorMap.put(fieldName,errorMessage);
            });

            return ResponseEntity.badRequest().body(errorMap);
        }

        try {
            studentUserService.create(
                    studentSignUpForm.getSName(),
                    studentSignUpForm.getSNickname(),  // 닉네임 따로 있으면 수정
                    studentSignUpForm.getSEmail(),
                    studentSignUpForm.getSPassword(),
                    studentSignUpForm.getSConfirmPassword()
            );
            return ResponseEntity.ok("회원가입 성공");
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
