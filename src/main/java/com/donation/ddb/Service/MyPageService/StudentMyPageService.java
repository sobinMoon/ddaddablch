package com.donation.ddb.Service.MyPageService;

import com.donation.ddb.Domain.PostComment;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Response.DonationStatusDTO;
import com.donation.ddb.Dto.Response.StudentMyPageResponseDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


}
