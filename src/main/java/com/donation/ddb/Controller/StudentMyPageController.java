package com.donation.ddb.Controller;


import com.donation.ddb.Domain.Exception.DataNotFoundException;
import com.donation.ddb.Dto.Request.StudentInfoUpdateResponseDTO;
import com.donation.ddb.Dto.Response.StudentMyPageResponseDTO;
import com.donation.ddb.Service.MyPageService.StudentMyPageService;
import com.donation.ddb.apiPayload.ApiResponse;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.code.status.SuccessStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/mypage/student")
@RequiredArgsConstructor
@Slf4j
//@PreAuthorize("hasRole('STUDENT')")
public class StudentMyPageController {

    private final StudentMyPageService studentMyPageService;

    //현재 로그인한 학생의 마이페이지 정보 조회
    @GetMapping()
    public ResponseEntity<ApiResponse<StudentMyPageResponseDTO>> getMyPageInfo(
    ){
        try{
            StudentMyPageResponseDTO myPageInfo=
                    studentMyPageService.getMyPageInfo();

            return ResponseEntity.ok(
                    ApiResponse.of(SuccessStatus.STUDENT_MYPAGE_INFO_RECEIVED,myPageInfo)
            );
        } catch(DataNotFoundException e){
            log.error("마이페이지 요청 도중 학생 못 찾음",e.getMessage());
            return ResponseEntity.status(ErrorStatus.STUDENT_USER_NOT_FOUND.getHttpStatus())
                    .body(ApiResponse.onFailure(
                            ErrorStatus.STUDENT_USER_NOT_FOUND.getCode(),
                            e.getMessage(),
                            null));
        }catch(Exception e){
            log.error("마이페이지 찾지 못함.",e.getMessage());
            return ResponseEntity.status(ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(ApiResponse.onFailure(
                            ErrorStatus._INTERNAL_SERVER_ERROR.getCode(),
                            e.getMessage(),
                            null));
        }
    }

    // 종합 프로필 수정 (닉네임 + 비밀번호 + 이미지)
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestPart(value = "data", required = false) @Valid StudentInfoUpdateResponseDTO updateDto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        try {
            String result = studentMyPageService.updateProfile(updateDto, profileImage);
            return ResponseEntity.ok().body(ApiResponse.onSuccess(result));
        } catch (IllegalArgumentException e) {
            log.error("프로필 업데이트 실패 - 잘못된 입력: {}", e.getMessage());
           //return ApiResponse.onFailure("INVALID_INPUT", e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("success","false","message","잘못된 입력으로 프로필 업데이트에 실패하였습니다",
                            "error",e.getMessage())
            );
        } catch (Exception e) {
            log.error("프로필 업데이트 실패", e);
            //return ApiResponse.onFailure("PROFILE_UPDATE_ERROR", "프로필 업데이트에 실패했습니다.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("success","false",
                            "message","서버 오류로 프로필 업데이트가 실패하였습니다.",
                            "error",e.getMessage())
            );
        }
    }

}
