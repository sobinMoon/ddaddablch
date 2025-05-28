package com.donation.ddb.Controller;


import com.donation.ddb.Dto.Response.StudentMyPageResponseDTO;
import com.donation.ddb.Service.MyPageService.StudentMyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mypage/student")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('STUDENT')")
public class StudentMyPageController {

//    private final StudentMyPageService studentMyPageService;
//
//    //현재 로그인한 학생의 마이페이지 정보 조회
//    @GetMapping
//    public ResponseEntity<StudentMyPageResponseDTO> getMyPageInfo(
//
//    ){
//        try{
//            StudentMyPageResponseDTO myPageInfo=
//                    studentMyPageService.getMyPageInfo();
//            return ResponseEntity.ok(myPageInfo);
//
//        } catch(Exception e){
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
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
//    }
//


}
