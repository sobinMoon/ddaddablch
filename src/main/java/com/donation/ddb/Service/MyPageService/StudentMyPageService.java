package com.donation.ddb.Service.MyPageService;

import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Response.StudentMyPageResponseDTO;
import com.donation.ddb.Repository.PostRepository.PostRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class StudentMyPageService {

    private final StudentUserRepository studentUserRepository;
    //private final DonationRepository donationRepository;
    //private final PostRepository postRepository;
    //
     //

    public StudentMyPageResponseDTO getMyPageInfo(){
        //현재 로그인한 사용자 정보 가져오기
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName(); //이메일 찾기

        StudentUser student=studentUserRepository.findBysEmail(currentUserEmail)
                .orElseThrow(()-> new RuntimeException("사용자를 찾을 수 없습니다."));

        return getMyPageInfo(student.getSId());
    }

    public StudentMyPageResponseDTO getMyPageInfo(Long studentId){
        StudentUser student= studentUserRepository.findById(studentId)
                .orElseThrow(()-> new RuntimeException("사용자 찾을 수 없습니다."));

        //기부 정보 조회

        //활동 정보 조회


        //지갑 주소는 필요없을 것 같아서 안넣음.
        return StudentMyPageResponseDTO.builder()
                .sId(student.getSId())
                .sName(student.getSName())
                .sNickname(student.getSNickname())
                .sEmail(student.getSEmail())
                .sProfileImage(student.getSProfileImage())
                .sCreatedAt(student.getCreatedAt())

                .totalDonationAmount()
                .totalDonationCount()
                .recentDonations()

                .totalPostCount()
                .totalCommentCount()
                .totalLikeCount()
                .recentPosts()
                .unreadNotificationCount(0)
                .build();


    }





}
