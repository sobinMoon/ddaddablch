package com.donation.ddb.Controller;


import com.donation.ddb.Dto.Request.DuplicateNicknameRequestDto;
import com.donation.ddb.Dto.Request.StudentSignUpForm;
import com.donation.ddb.Dto.Response.DuplicateNicknameResponseDto;
import com.donation.ddb.Service.StudentUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.naming.Binding;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/sign-up")
public class StudentUserController {

    @Autowired
    private final StudentUserService studentUserService;

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
            Long id=studentUserService.createUser(
                    studentSignUpForm.getSName(),
                    studentSignUpForm.getSNickname(),
                    studentSignUpForm.getSEmail(),
                    studentSignUpForm.getSPassword(),
                    studentSignUpForm.getSConfirmPassword()
            );
            return ResponseEntity.ok(
              Map.of("success",true,
                      "message","회원가입 성공했습니다.")

            );
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
