package com.donation.ddb.Service.DonationService;

import com.donation.ddb.Domain.*;
import com.donation.ddb.Domain.Exception.DataNotFoundException;
import com.donation.ddb.Repository.CampaignRepository.CampaignRepository;
import com.donation.ddb.Repository.DonationRepository.DonationRepository;
import com.donation.ddb.Repository.NotificationRepository;
import com.donation.ddb.Repository.OrganizationUserRepository;
import com.donation.ddb.Repository.StudentUserRepository;
import com.donation.ddb.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationService {

    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final StudentUserRepository studentUserRepository;
    private final OrganizationUserRepository organizationUserRepository;
    private final NotificationService notificationService;

    // 트랜잭션이 중복인지 확인
    public Boolean isDuplicateTransaction(String transactionHash){
        Boolean isduplicate=donationRepository.existsByTransactionHash(transactionHash);
        log.info("트랜잭션 중복 확인 - Hash: {}, 중복 여부 : {}",transactionHash,isduplicate);
        return isduplicate;
    }

    // 기부 기록하기 -> User가 메타마스크 지갑 인증했을 때 호출하기
    // -> 기부 성공or실패하면 Status 바꾸기
    @Transactional
    public Donation recordDonation(String hash,
                                   String donorWalletAddress,
                                   String beneficiaryWalletAddress,
                                   BigDecimal amount, //이더 단위
                                   Long campaignId,
                                   Long userId,
                                   String message){

        log.info("기부 기록 저장 시작 - Hash: {} Amount: {} ETH ",hash,amount);
        try{
            // 중복 트랜잭션인거 확인 필요하나? -> 컨트롤러 앞에서 isduplicate함수 호출되면 굳이

            // 기부자 조회하기
            StudentUser studentUser=studentUserRepository.findById(userId)
                 .orElseThrow(() -> {
                     log.error("캠페인을 찾을 수 없습니다. ID: {}", campaignId);
                     return new IllegalArgumentException("존재하지 않는 캠페인입니다: " + campaignId);
                 });
            // 캠페인 조회하기
            Campaign campaign = campaignRepository.findById(campaignId)
                    .orElseThrow(() -> {
                        log.error("캠페인을 찾을 수 없습니다. ID: {}", campaignId);
                        return new IllegalArgumentException("존재하지 않는 캠페인입니다: " + campaignId);
                    });
            // Donation 엔티티 생성
            Donation newDonation = Donation.builder()
                    .transactionHash(hash)
                    .donorWalletAddress(donorWalletAddress)
                    .campaignWalletAddress(beneficiaryWalletAddress)
                    .amount(amount)
                    .message(message)
                    .studentUser(studentUser)
                    .campaign(campaign)
                    .status(DonationStatus.SUCCESS) // 초기 상태는 PENDING -> 그니까 기부하기 전에 메타마스크 인증하고 해야되나
                    .build();

            //DB 에 저장하기
            Donation savedDonation=donationRepository.save(newDonation);

            // 기부 완료 알림 생성
            notificationService.createDonationCompleteNotification(
                    studentUser.getSId(),           // 기부한 학생 ID
                    campaign.getCName(),        // 캠페인 이름
                    savedDonation.getDId()      // 기부 ID
            );

            log.info("기부 기록 저장 완료 - ID: {}, Hash: {}", savedDonation.getDId(), hash);
            return savedDonation;


        } catch (Exception e) {
            log.error("기부 기록 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("기부 기록 저장에 실패했습니다", e);
        }

    }

    //지갑 주소로 학생 찾기
    private StudentUser findStudentByWalletAddress(String walletAddress){
        String lowerWallet=walletAddress.toLowerCase();
        // 모든 학생을 조회해서 지갑 주소 리스트에서 찾기
            List<StudentUser> allStudents = studentUserRepository.findAll();
            for (StudentUser student : allStudents) {
                if (student.hasWallet(lowerWallet)) {
                    log.info("기부자 찾음 - Student ID: {}, Name: {}, Wallet: {}",
                            student.getSId(), student.getSName(), walletAddress);
                    return student;
                }
            }
        log.error("등록되지 않은 기부자 지갑 주소: {}", walletAddress);
        throw new IllegalArgumentException("등록되지 않은 기부자 지갑 주소입니다: " + walletAddress);
    }

    // 지갑 주소로 캠페인 찾기
    private Campaign findCampaignByWalletAddress(String walletAddress) {

         Campaign campaign=campaignRepository.findBycWalletAddress(walletAddress)
                 .orElseThrow(()-> new DataNotFoundException("수혜자 주소에 맞는 캠페인이 존재하지 않습니다."));
         return campaign;
    }

    // 기부 성공 or 실패 시 donationStatus 업그레이드
    @Transactional
    public Donation updatedDonationStatus(String transactionHash,DonationStatus status){
        log.info("기부 상태 업데이트 - Hash : {}, Status: {} ",transactionHash,status);

        Donation donation=donationRepository.findByTransactionHash(transactionHash)
                .orElseThrow(()-> new DataNotFoundException("해당 해시의 기부 기록을 확인할 수 없습니다. "));


        donation.setStatus(status);
        donationRepository.save(donation);

        log.info("기부 상태 업데이트 완료 - ID: {}, 새로운 상태: {}", donation.getDId(), status);
        return donation;
    }


}
