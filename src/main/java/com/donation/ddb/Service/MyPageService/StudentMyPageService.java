package com.donation.ddb.Service.MyPageService;

import com.donation.ddb.Domain.Exception.DataNotFoundException;
import com.donation.ddb.Domain.StudentNFT;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Request.StudentInfoUpdatePwdDTO;
import com.donation.ddb.Dto.Request.StudentInfoUpdateRequestDTO;
import com.donation.ddb.Dto.Response.DonationStatusDTO;
import com.donation.ddb.Dto.Response.StudentMyPageResponseDTO;
//import com.donation.ddb.ImageStore;
import com.donation.ddb.ImageStore;
import com.donation.ddb.Repository.DonationRepository.DonationRepository;
import com.donation.ddb.Repository.PostCommentRepository.PostCommentRepository;
import com.donation.ddb.Repository.PostRepository.PostRepository;
import com.donation.ddb.Repository.StudentNFTRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import com.donation.ddb.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final StudentNFTRepository studentNFTRepository;

    public StudentMyPageResponseDTO getMyPageInfo() {

        try {
            // 현재 로그인한 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName(); //이메일 찾기

            StudentUser student = studentUserRepository.findBysEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 기부 통계 조회 - 쿼리 한개로 조회 -> total amount, total count
            DonationStatusDTO donationStatus =
                    donationRepository.getDonationStatsByStudentId(student.getSId());

            // 최근 기부 내역 조회
            List<StudentMyPageResponseDTO.DonationSummaryDTO> recentDonations =
                    donationRepository.getRecentDonationSummary(student.getSId(), 5);

            //활동 정보 조회 -> 글이랑 댓글 리스트
            List<StudentMyPageResponseDTO.RecentPostDTO> posts = postRepository.findRecentPostsByStudentId(student.getSId());
            List<StudentMyPageResponseDTO.PostCommentDTO> comments = postCommentRepository.findRecentCommentsByStudentId(student.getSId());

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

    // 닉네임 + 이미지 업데이트 (비밀번호 확인 필요)
    @Transactional
    public String updateProfile(StudentInfoUpdateRequestDTO updateDto, MultipartFile profileImage) {
        StudentUser student = getCurrentStudent();
        boolean hasChanges = false;
        log.info("프로필 업데이트 시작: 사용자ID={}", student.getSId());

        // 현재 비밀번호 확인 (닉네임 변경시 필수)
        if (updateDto != null && StringUtils.hasText(updateDto.getCurrentPassword())) {
            if (!passwordEncoder.matches(updateDto.getCurrentPassword(), student.getSPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            log.info("비밀번호 확인 완료: 사용자ID={}", student.getSId());
        } else if (updateDto != null && StringUtils.hasText(updateDto.getNickname())) {
            throw new IllegalArgumentException("닉네임 변경을 위해 현재 비밀번호를 입력해주세요.");
        }

        // 닉네임 수정
        if (updateDto != null && StringUtils.hasText(updateDto.getNickname())) {
            String newNickname = updateDto.getNickname().trim();
            log.info("닉네임 업데이트 시도: 기존={}, 새로운={}", student.getSNickname(), newNickname);

            // 현재 닉네임과 다른 경우에만 중복 체크
            if (!newNickname.equals(student.getSNickname())) {
                if (studentUserRepository.findBysNickname(newNickname).isPresent()) {
                    throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
                }
                student.setSNickname(newNickname);
                hasChanges = true;
                log.info("닉네임 업데이트 완료: 사용자ID={}, 새 닉네임={}", student.getSId(), newNickname);
            } else {
                log.info("닉네임이 기존과 동일함: {}", newNickname);
            }
        }

        // 프로필 이미지 수정
        if (profileImage != null && !profileImage.isEmpty()) {
            updateProfileImageInternal(student, profileImage);
            hasChanges = true;
        }

        if (hasChanges) {
            studentUserRepository.save(student);
            return "프로필이 성공적으로 업데이트되었습니다.";
        } else {
            return "변경사항이 없습니다.";
        }
    }

    // 비밀번호 변경 전용 메서드
    @Transactional
    public String updateProfilepwd(StudentInfoUpdatePwdDTO updateDto, MultipartFile profileImage) {
        StudentUser student = getCurrentStudent();
        boolean hasChanges = false;
        log.info("비밀번호 업데이트 시작: 사용자ID={}", student.getSId());

        // 비밀번호 수정
        if (updateDto != null && StringUtils.hasText(updateDto.getCurrentPassword()) &&
                StringUtils.hasText(updateDto.getNewPassword())) {
            updatePasswordInternal(student, updateDto.getCurrentPassword(),
                    updateDto.getNewPassword(), updateDto.getConfirmNewPassword());
            hasChanges = true;
        }

        // 프로필 이미지 수정
        if (profileImage != null && !profileImage.isEmpty()) {
            updateProfileImageInternal(student, profileImage);
            hasChanges = true;
        }

        if (hasChanges) {
            studentUserRepository.save(student);
            return "프로필이 성공적으로 업데이트되었습니다.";
        } else {
            return "변경사항이 없습니다.";
        }
    }

    // 현재 로그인한 학생 정보 조회
    private StudentUser getCurrentStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        String currentUserEmail = authentication.getName();
        return studentUserRepository.findBysEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalStateException("로그인된 사용자를 찾을 수 없습니다."));
    }

    // 비밀번호 업데이트 (내부 로직)
    private void updatePasswordInternal(StudentUser student, String currentPassword,
                                        String newPassword, String confirmNewPassword) {
        // 입력값 검증
        if (!StringUtils.hasText(currentPassword)) {
            throw new IllegalArgumentException("현재 비밀번호를 입력해주세요.");
        }
        if (!StringUtils.hasText(newPassword)) {
            throw new IllegalArgumentException("새 비밀번호를 입력해주세요.");
        }
        if (!StringUtils.hasText(confirmNewPassword)) {
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

        log.info("비밀번호 업데이트 완료: 사용자ID={}", student.getSId());
    }

    // 프로필 이미지 업데이트 (내부 로직)
    private void updateProfileImageInternal(StudentUser student, MultipartFile profileImage) {
        try {
            // 이미지 파일 검증
            if (profileImage.getSize() > 5 * 1024 * 1024) { // 5MB 제한
                throw new IllegalArgumentException("이미지 파일 크기는 5MB를 초과할 수 없습니다.");
            }

            String contentType = profileImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
            }

            // 기존 이미지 삭제 (기본 이미지가 아닌 경우에만)
            String currentImage = student.getSProfileImage();
            if (currentImage != null && !currentImage.contains("default_profile.png")) {
                ImageStore.deleteImage(currentImage);
            }

            // 새 이미지 저장 (플랫폼 독립적 경로)
            String imagePath = ImageStore.storeImage(profileImage,
                    "/users/" + student.getSId() + "/");
            student.setSProfileImage(imagePath);

            log.info("프로필 이미지 업데이트 완료: 사용자ID={}, 이미지경로={}",
                    student.getSId(), imagePath);

        } catch (IllegalArgumentException e) {
            throw e; // 검증 오류는 그대로 전달
        } catch (Exception e) {
            log.error("프로필 이미지 업데이트 실패", e);
            throw new RuntimeException("프로필 이미지 업데이트에 실패했습니다.", e);
        }
    }

    // storeNFTImage

    /**
     * NFT 이미지 저장
     */
    public String storeNFTImage(Long studentId, MultipartFile image) throws IOException {
        // 학생 존재 확인
        StudentUser student = studentUserRepository.findById(studentId)
                .orElseThrow(() -> new DataNotFoundException("학생을 찾을 수 없습니다."));

        // ImageStore를 사용하여 이미지 저장
        String imageUrl = ImageStore.storeImage(image, "nft/" + studentId + "/");

        // 중복 이미지 URL 체크 (선택사항)
        if (studentNFTRepository.existsByImageUrl(imageUrl)) {
            throw new IllegalArgumentException("이미 존재하는 이미지입니다.");
        }

        // StudentNFT 엔티티 생성 및 저장
        StudentNFT studentNFT = StudentNFT.builder()
                .studentUser(student)
                .imageUrl(imageUrl)
                .build();

        studentNFTRepository.save(studentNFT);

        log.info("NFT 이미지 저장 완료 - 학생 ID: {}, 파일: {}", studentId, imageUrl);
        return "NFT 이미지가 성공적으로 저장되었습니다.";
    }

    /**
     * 학생의 NFT 이미지 URL 목록 조회
     */
    public List<String> getNFTImages(Long studentId) {
        // 학생 존재 확인 및 조회
        StudentUser student = studentUserRepository.findById(studentId)
                .orElseThrow(() -> new DataNotFoundException("학생을 찾을 수 없습니다."));

        // 바뀐 메서드 사용
        List<StudentNFT> nftList = studentNFTRepository.findByStudentUserOrderByCreatedAtDesc(student);

        return nftList.stream()
                .map(StudentNFT::getImageUrl)
                .collect(Collectors.toList());
    }

}