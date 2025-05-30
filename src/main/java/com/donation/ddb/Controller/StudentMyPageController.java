package com.donation.ddb.Controller;


import com.donation.ddb.Domain.Exception.DataNotFoundException;
import com.donation.ddb.Dto.Response.StudentMyPageResponseDTO;
import com.donation.ddb.Service.MyPageService.StudentMyPageService;
import com.donation.ddb.apiPayload.ApiResponse;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.code.status.SuccessStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            //return ResponseEntity.ok(myPageInfo);
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

    //학생 기부 내역 조회
    //학생 프로필 정보 수정

//    //공개 프로필 필요할까?
//    @GetMapping("/{studentId}")
//    public ResponseEntity<StudentMyPageResponseDTO> getMyPageInfo(@PathVariable Long studentId){
//        try{
//            StudentMyPageResponseDTO myPageResponseDTO
//                    =studentMyPageService.getMyPageInfo(studentId);
//
//            return ResponseEntity.ok(myPageResponseDTO);
//        }catch (Exception e){
//            return ResponseEntity.badRequest().build();
//        }
//   }



}
