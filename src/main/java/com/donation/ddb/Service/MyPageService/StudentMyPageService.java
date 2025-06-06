package com.donation.ddb.Service.MyPageService;

import com.donation.ddb.Domain.PostComment;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Request.StudentInfoUpdateResponseDTO;
import com.donation.ddb.Dto.Response.DonationStatusDTO;
import com.donation.ddb.Dto.Response.StudentMyPageResponseDTO;
//import com.donation.ddb.ImageStore;
import com.donation.ddb.Repository.DonationRepository.DonationRepository;
import com.donation.ddb.Repository.PostCommentRepository.PostCommentRepository;
import com.donation.ddb.Repository.PostRepository.PostRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import com.donation.ddb.Repository.projection.PostWithCount;
import com.donation.ddb.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly=true)
public class StudentMyPageService {

    private final StudentUserRepository studentUserRepository;
    private final DonationRepository donationRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    public StudentMyPageResponseDTO getMyPageInfo(){

        try {
            // 현재 로그인한 사용자 정보 가져오기
            Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName(); //이메일 찾기

            StudentUser student=studentUserRepository.findBysEmail(currentUserEmail)
                .orElseThrow(()-> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 기부 통계 조회 - 쿼리 한개로 조회 -> total amount, total count
            DonationStatusDTO donationStatus=
                    donationRepository.getDonationStatsByStudentId(student.getSId());

            // 최근 기부 내역 조회
            List<StudentMyPageResponseDTO.DonationSummaryDTO> recentDonations =
                    donationRepository.getRecentDonationSummary(student.getSId(), 5);

            //활동 정보 조회 -> 글이랑 댓글 리스트
            List<StudentMyPageResponseDTO.RecentPostDTO> posts = postRepository.findRecentPostsByStudentId(student.getSId());
            List<StudentMyPageResponseDTO.PostCommentDTO> comments=postCommentRepository.findRecentCommentsByStudentId(student.getSId());

            //알림,통계 정보 조회 추후 구현하기

            //최종 응답 DTO 생성
            StudentMyPageResponseDTO response = StudentMyPageResponseDTO.builder()
                    .sId(student.getSId())
                    .sName(student.getSName())
                    .sNickname(student.getSNickname())
                    .sEmail(student.getSEmail())
                    .sProfileImage(student.getSProfileImage())
                    .walletAddresses(student.getWalletList()) // JSON으로 저장된 지갑 목록
                    .createdAt(student.getCreatedAt())
                   // .campaignId()
                    // 기부 관련 정보
                    .totalDonationAmount(donationStatus.getTotalAmount())
                    .totalDonationCount(donationStatus.getTotalCount())
                    .recentDonations(convertToResponseDTOs(recentDonations))

                    // 통계 정보
                    //.stats(stats)

                    // 추후 구현할 필드들
                    .recentPosts(posts)
                    .recentComments(comments)
                    .unreadNotifications(notificationService.getUnreadNotifications(student.getSId()))
                    // .totalCommentCount(activityStats.getTotalCommentCount())
                    // .unreadNotificationCount(unreadNotificationCount)
                    .build();

            log.info("마이페이지 정보 조회 완료: 사용자ID={}", student.getSId());
            return response;

        } catch (Exception e) {
            log.error("마이페이지 정보 조회 중 오류 발생", e);
            throw new RuntimeException("마이페이지 정보를 불러오는데 실패했습니다.", e);
        }

        }

        //QueryDSL DTO를 Response DTO로 변환
        private List<StudentMyPageResponseDTO.DonationSummaryDTO> convertToResponseDTOs(
            List<StudentMyPageResponseDTO.DonationSummaryDTO> queryDslDTOs) {

        if (queryDslDTOs == null || queryDslDTOs.isEmpty()) {
            return new ArrayList<>();
        }

        return queryDslDTOs.stream()
                .map(dto -> StudentMyPageResponseDTO.DonationSummaryDTO.builder()
                        .donationId(dto.getDonationId())
                        .campaignName(dto.getCampaignName())
                        .donationAmount(dto.getDonationAmount())
                        .donationDate(dto.getDonationDate())
                        .transactionHash(dto.getTransactionHash())
                        .donationStatus(dto.getDonationStatus())
                        .campaignId(dto.getCampaignId())
                        .build())
                .collect(Collectors.toList());
        }


    //  종합 프로필 수정 (닉네임, 비밀번호, 이미지)
    @Transactional
    public String updateProfile(StudentInfoUpdateResponseDTO updateDto, MultipartFile profileImage) {
        StudentUser student = getCurrentStudent();
        boolean hasChanges = false;

        // 닉네임 수정
        if (updateDto != null && updateDto.getSNickname() != null && !updateDto.getSNickname().trim().isEmpty()) {
            student.setSNickname(updateDto.getSNickname().trim());
            hasChanges = true;
            log.info("닉네임 업데이트: 사용자ID={}, 새 닉네임={}", student.getSId(), updateDto.getSNickname());
        }

        // 비밀번호 수정
        if (updateDto != null && updateDto.getCurrentPassword() != null && updateDto.getNewPassword() != null) {
            updatePasswordInternal(student, updateDto.getCurrentPassword(), updateDto.getNewPassword(), updateDto.getConfirmNewPassword());
            hasChanges = true;
        }

//        // 프로필 이미지 수정
//        if (profileImage != null && !profileImage.isEmpty()) {
//            updateProfileImageInternal(student, profileImage);
//            hasChanges = true;
//        }

        if (hasChanges) {
            studentUserRepository.save(student);
            return "프로필이 성공적으로 업데이트되었습니다.";
        } else {
            return "변경사항이 없습니다.";
        }
    }
    // === 내부 헬퍼 메서드들 ===
    private StudentUser getCurrentStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        return studentUserRepository.findBysEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    private void updatePasswordInternal(StudentUser student, String currentPassword, String newPassword, String confirmNewPassword) {
        // 입력값 검증
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("현재 비밀번호를 입력해주세요.");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("새 비밀번호를 입력해주세요.");
        }
        if (confirmNewPassword == null || confirmNewPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("새 비밀번호 확인을 입력해주세요.");
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, student.getSPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 확인
        if (!newPassword.equals(confirmNewPassword)) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        // 현재 비밀번호와 새 비밀번호가 같은지 확인
        if (passwordEncoder.matches(newPassword, student.getSPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        // 비밀번호 암호화 후 저장
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        student.setSPassword(encodedNewPassword);
    }

//    private void updateProfileImageInternal(StudentUser student, MultipartFile profileImage) {
//        try {
//            // 기존 이미지 삭제 (있다면)
//            if (student.getSProfileImage() != null) {
//                ImageStore.deleteImage(student.getSProfileImage());
//            }
//
//            // 새 이미지 저장
//            String imagePath = ImageStore.storeImage(profileImage,
//                    "\\users\\" + student.getSId() + "\\");
//            student.setSProfileImage(imagePath);
//
//            log.info("프로필 이미지 업데이트 완료: 사용자ID={}, 이미지경로={}",
//                    student.getSId(), imagePath);
//
//        } catch (Exception e) {
//            log.error("프로필 이미지 업데이트 실패", e);
//            throw new RuntimeException("프로필 이미지 업데이트에 실패했습니다.", e);
//        }
}
