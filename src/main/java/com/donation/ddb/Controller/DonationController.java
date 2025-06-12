//package com.donation.ddb.Controller;
//
//import com.donation.ddb.Domain.Campaign;
//import com.donation.ddb.Domain.Donation;
//import com.donation.ddb.Domain.DonationStatus;
//import com.donation.ddb.Domain.Exception.DataNotFoundException;
//import com.donation.ddb.Dto.Request.DonationRecordRequestDTO;
//import com.donation.ddb.Dto.Request.DonationRequestDTO;
//import com.donation.ddb.Dto.Request.DonationStatusUpdateDTO;
//import com.donation.ddb.Dto.Response.DonationResponseDTO;
//import com.donation.ddb.Repository.OrganizationUserRepository;
//import com.donation.ddb.Service.DonationService.BlockchainService;
//import com.donation.ddb.Service.DonationService.DonationService;
//import com.donation.ddb.apiPayload.ApiResponse;
//import com.donation.ddb.apiPayload.code.status.ErrorStatus;
//import com.donation.ddb.apiPayload.code.status.SuccessStatus;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cglib.core.Block;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.web3j.protocol.core.methods.response.TransactionReceipt;
//import org.web3j.utils.Convert;
//
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//
//import org.springframework.stereotype.Controller;
//
//@Controller
//@RequestMapping("/api/donations")
//@RequiredArgsConstructor
//@Slf4j
//public class DonationController {
//    private final BlockchainService blockchainService;
//    private final OrganizationUserRepository organizationUserRepository;
//    //private final BlockchainService blockchainService;
//    private final DonationService donationService;
//
////    //기부
////    @PostMapping("/donate")
////    public ResponseEntity<Map<String, String>>
////    donate(@RequestBody DonationRequestDTO request) {
////        try {
////            //ETH를 wei로 변환 (1eth=10^18 wei)
////            BigInteger weiAmount = Convert.toWei(
////                    request.getAmount().toString(),
////                    Convert.Unit.ETHER
////            ).toBigInteger();
////
////            // 블록체인에 트랜잭션 전송
////            CompletableFuture<TransactionReceipt> future =
////                    blockchainService.donate(request.getBeneficiary(), weiAmount);
////            // 결과 반환 (트랜잭션 해시)
////            Map<String, String> response = new HashMap<>();
////            response.put("status", "Transactio 보내짐.");
////            response.put("transactionHash", future.join().getTransactionHash());
////
////            return ResponseEntity.accepted().body(response);
////        } catch (Exception e) {
////            e.printStackTrace();
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
////                    .body(Collections.singletonMap("error", e.getMessage()));
////        }
////    }
//
//    //프론트에서 메타마스크 지갑 인증 완료 하면 호출하도록
//    @PostMapping("/record")
//    //@PreAuthorize("hasRole('STUDENT')")
//    public ResponseEntity<?> recordDonation(@Valid @RequestBody DonationRecordRequestDTO request, BindingResult bindingResult){
//        try{
//            if (bindingResult.hasErrors()) {
//                Map<String, String> errorMap = new HashMap<>();
//                bindingResult.getFieldErrors().forEach(error -> {
//                    errorMap.put(error.getField(), error.getDefaultMessage());
//                    log.warn("로그인 유효성 검증 실패: {} - {}", error.getField(), error.getDefaultMessage());
//                });
//                return ResponseEntity.status(ErrorStatus._BAD_REQUEST.getHttpStatus())
//                        .body(ApiResponse.onFailure(
//                                ErrorStatus._BAD_REQUEST.getCode(),
//                                "입력값이 올바르지 않습니다.",
//                                errorMap));
//            }
//
//            //해시검증
//            String transactionHash=request.getTransactionHash();
//            if(transactionHash == null || transactionHash.trim().isEmpty()){
////                Map<String,String> errorResponse=new HashMap<>();
////                errorResponse.put("error","트랜잭션 해시가 필요합니다.");
////                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//                return ResponseEntity.status(ErrorStatus.DONATION_MISSING_TRANSACTION_HASH.getHttpStatus())
//                        .body(
//                        ApiResponse.onFailure(
//                                ErrorStatus.DONATION_MISSING_TRANSACTION_HASH.getCode(),
//                                ErrorStatus.DONATION_MISSING_TRANSACTION_HASH.getMessage(),
//                                null)
//                );
//            }
//
//            // 블록체인에서 트랜잭션 정보 조회 및 검증 -> 가나슈에 있는 내용과 일치하는지
//            boolean isValidTransaction=blockchainService.verifyTransaction(
//                    transactionHash,
//                    request.getDonorWalletAddress(),
//                    request.getCampaignWalletAddress(),
//                    request.getAmount() //이더 단위 금액
//            );
//
//            if (!isValidTransaction) {
//                return ResponseEntity.status(ErrorStatus.DONATION_INVALID_TRANSACTION.getHttpStatus())
//                        .body(ApiResponse.onFailure(
//                                ErrorStatus.DONATION_INVALID_TRANSACTION.getCode(),
//                                ErrorStatus.DONATION_INVALID_TRANSACTION.getMessage(),
//                                null));
//            }
//
//            // 중복 기부 기록 확인 -> 이미 기록된건지
//            if (donationService.isDuplicateTransaction(transactionHash)) {
//                return ResponseEntity.status(ErrorStatus.DONATION_DUPLICATE_TRANSACTION.getHttpStatus())
//                        .body(ApiResponse.onFailure(
//                                ErrorStatus.DONATION_DUPLICATE_TRANSACTION.getCode(),
//                                ErrorStatus.DONATION_DUPLICATE_TRANSACTION.getMessage(),
//                                null));
//            }
//            //기부 기록 저장
//            Donation response=donationService.recordDonation(
//                    transactionHash,
//                    request.getDonorWalletAddress(),
//                    request.getCampaignWalletAddress(),
//                    request.getAmount(),
//                    request.getCampaignId(),
//                    request.getUserId(),
//                    request.getMessage()
//            );
//
//            return ResponseEntity.ok(ApiResponse.of(SuccessStatus.DONATION_RECORDED, null));
//
//        }catch (IllegalArgumentException e) {
//            log.error("기부 기록 중 데이터 오류: ", e);
//            if (e.getMessage().contains("학생")) {
//                return ResponseEntity.status(ErrorStatus.STUDENT_USER_NOT_FOUND.getHttpStatus())
//                        .body(ApiResponse.onFailure(
//                                ErrorStatus.STUDENT_USER_NOT_FOUND.getCode(),
//                                ErrorStatus.STUDENT_USER_NOT_FOUND.getMessage(),
//                                null));
//            } else if (e.getMessage().contains("캠페인")) {
//                return ResponseEntity.status(ErrorStatus.CAMPAIGN_NOT_FOUND.getHttpStatus())
//                        .body(ApiResponse.onFailure(
//                                ErrorStatus.CAMPAIGN_NOT_FOUND.getCode(),
//                                ErrorStatus.CAMPAIGN_NOT_FOUND.getMessage(),
//                                null));
//            }
//            return ResponseEntity.status(ErrorStatus._BAD_REQUEST.getHttpStatus())
//                    .body(ApiResponse.onFailure(
//                            ErrorStatus._BAD_REQUEST.getCode(),
//                            e.getMessage(),
//                            null));
//        } catch (Exception e) {
//            log.error("기부 기록 중 오류 발생: ", e);
//            return ResponseEntity.status(ErrorStatus.DONATION_RECORD_FAILED.getHttpStatus())
//                    .body(ApiResponse.onFailure(
//                            ErrorStatus.DONATION_RECORD_FAILED.getCode(),
//                            ErrorStatus.DONATION_RECORD_FAILED.getMessage(),
//                            null));
//        }
//    }
//
//    //patch -> 부분 수정
//    @PatchMapping("/status")
//    public ResponseEntity<?> updateDonationStatus(
//            @RequestBody @Valid DonationStatusUpdateDTO request
//            ){
//        try{
//            String txHash=request.getTransactionHash();
//            String newStatus=request.getStatus();
//
//            if (txHash == null || txHash.trim().isEmpty() || newStatus == null || newStatus.trim().isEmpty()) {
//                return ResponseEntity.status(ErrorStatus.DONATION_MISSING_REQUIRED_FIELDS.getHttpStatus())
//                        .body(ApiResponse.onFailure(
//                                ErrorStatus.DONATION_MISSING_REQUIRED_FIELDS.getCode(),
//                                ErrorStatus.DONATION_MISSING_REQUIRED_FIELDS.getMessage(),
//                                null));
//            }
//            DonationStatus statusEnum;
//
//            try {
//                statusEnum = DonationStatus.valueOf(newStatus.toUpperCase()); // 문자열 -> enum
//            }catch (IllegalArgumentException e) {
//                return ResponseEntity.status(ErrorStatus.DONATION_INVALID_STATUS.getHttpStatus())
//                        .body(ApiResponse.onFailure(
//                                ErrorStatus.DONATION_INVALID_STATUS.getCode(),
//                                ErrorStatus.DONATION_INVALID_STATUS.getMessage() + ": " + newStatus,
//                                null));
//            }
//
//            //상태 업데이트
//            Donation updated=donationService.updatedDonationStatus(txHash,statusEnum);
//
//            //return ResponseEntity.ok(ApiResponse.of(SuccessStatus.DONATION_RECORDED, null));
//            return ResponseEntity.ok(ApiResponse.of(SuccessStatus.DONATION_STATUS_UPDATED,null));
//        }catch (DataNotFoundException e) {
//            log.error("기부 기록을 찾을 수 없음: ", e);
//            return ResponseEntity.status(ErrorStatus.DONATION_NOT_FOUND.getHttpStatus())
//                    .body(ApiResponse.onFailure(
//                            ErrorStatus.DONATION_NOT_FOUND.getCode(),
//                            ErrorStatus.DONATION_NOT_FOUND.getMessage(),
//                            null));
//        } catch (Exception e) {
//            log.error("상태 업데이트 중 오류 발생: ", e);
//            return ResponseEntity.status(ErrorStatus.DONATION_STATUS_UPDATE_FAILED.getHttpStatus())
//                    .body(ApiResponse.onFailure(
//                            ErrorStatus.DONATION_STATUS_UPDATE_FAILED.getCode(),
//                            ErrorStatus.DONATION_STATUS_UPDATE_FAILED.getMessage(),
//                            null));
//        }
//
//
//    }
//
//    //수혜자 잔액 조회
//    @GetMapping("/balance/{address}")
//    public ResponseEntity<Map<String,String>> getBalance(@PathVariable("address") String address) {
//        try {
////            if((organizationUserRepository.findByoWalletAddress(address)).isEmpty()){
////                Map<String, String> errorResponse = new HashMap<>();
////                errorResponse.put("error", "존재하지 않는 수혜자 주소입니다.");
////                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
////            }
//
//
//            BigInteger balance = blockchainService.getBalance(address);
//            BigDecimal ethbalance=new BigDecimal(balance).divide(BigDecimal.TEN.pow(18));
//
//
//            Map<String, String> response = new HashMap<>();
//            response.put("address", address);
//            response.put("balance", ethbalance.toString());
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("error", "잔액 조회 중 오류가 발생했습니다: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//
//
//    }
//
//
////    //수혜자가 기부금 인출
////    @PostMapping("/withdraw/{beneficiaryAddress}")
////    public ResponseEntity<?> withdrawFunds(@PathVariable("beneficiaryAddress") String beneficiaryAddress) {
////        try {
////            String txHash=blockchainService.withdrawFunds(beneficiaryAddress);
////
////            Map<String, Object> result = new HashMap<>();
////            result.put("transactionHash", txHash);
////            result.put("status", "success");
////
////            return ResponseEntity.ok(result);
////        } catch(Exception e){
////            return ResponseEntity.badRequest().body("인출 실패: " + e.getMessage());
////
////        }
////    }
//
//}
package com.donation.ddb.Controller;

import com.donation.ddb.Domain.Campaign;
import com.donation.ddb.Domain.CustomUserDetails;
import com.donation.ddb.Domain.Donation;
import com.donation.ddb.Domain.DonationStatus;
import com.donation.ddb.Domain.Exception.DataNotFoundException;
import com.donation.ddb.Dto.Request.DonationRecordRequestDTO;
import com.donation.ddb.Dto.Request.DonationRequestDTO;
import com.donation.ddb.Dto.Request.DonationStatusUpdateDTO;
import com.donation.ddb.Dto.Request.NftStoreRequestDTO;
import com.donation.ddb.Dto.Response.DonationResponseDTO;
import com.donation.ddb.Repository.OrganizationUserRepository;
import com.donation.ddb.Service.DonationService.BlockchainService;
import com.donation.ddb.Service.DonationService.DonationService;
import com.donation.ddb.apiPayload.ApiResponse;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.code.status.SuccessStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Block;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/api/donations")
@RequiredArgsConstructor
@Slf4j
public class DonationController {
    private final BlockchainService blockchainService;
    private final OrganizationUserRepository organizationUserRepository;
    private final DonationService donationService;

    //프론트에서 메타마스크 지갑 인증 완료 하면 호출하도록
    @PostMapping("/record")
    //@PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> recordDonation(@Valid @RequestBody DonationRecordRequestDTO request, BindingResult bindingResult) {
        log.info("🚀 기부 기록 API 호출 시작");
        log.info("📝 요청 데이터: {}", request);

        try {
            if (bindingResult.hasErrors()) {
                log.warn("❌ 유효성 검증 실패");
                Map<String, String> errorMap = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error -> {
                    errorMap.put(error.getField(), error.getDefaultMessage());
                    log.warn("유효성 검증 실패: {} - {}", error.getField(), error.getDefaultMessage());
                });
                return ResponseEntity.status(ErrorStatus._BAD_REQUEST.getHttpStatus())
                        .body(ApiResponse.onFailure(
                                ErrorStatus._BAD_REQUEST.getCode(),
                                "입력값이 올바르지 않습니다.",
                                errorMap));
            }

            //해시검증
            String transactionHash = request.getTransactionHash();
            log.info("🔍 트랜잭션 해시 검증: {}", transactionHash);

            if (transactionHash == null || transactionHash.trim().isEmpty()) {
                log.error("❌ 트랜잭션 해시가 없음");
                return ResponseEntity.status(ErrorStatus.DONATION_MISSING_TRANSACTION_HASH.getHttpStatus())
                        .body(ApiResponse.onFailure(
                                ErrorStatus.DONATION_MISSING_TRANSACTION_HASH.getCode(),
                                ErrorStatus.DONATION_MISSING_TRANSACTION_HASH.getMessage(),
                                null));
            }

            // 🔍 중복 기부 기록 확인 -> 이미 기록된건지
            log.info("🔄 중복 트랜잭션 확인: {}", transactionHash);
            if (donationService.isDuplicateTransaction(transactionHash)) {
                log.warn("⚠️ 이미 기록된 트랜잭션: {}", transactionHash);
                return ResponseEntity.status(ErrorStatus.DONATION_DUPLICATE_TRANSACTION.getHttpStatus())
                        .body(ApiResponse.onFailure(
                                ErrorStatus.DONATION_DUPLICATE_TRANSACTION.getCode(),
                                ErrorStatus.DONATION_DUPLICATE_TRANSACTION.getMessage(),
                                null));
            }

            // 🔍 블록체인에서 트랜잭션 정보 조회 및 검증 -> 가나슈에 있는 내용과 일치하는지
            log.info("⛓️ 블록체인 트랜잭션 검증 시작");
            log.info("📊 검증 데이터 - 해시: {}, 기부자: {}, 수혜자: {}, 금액: {}",
                    transactionHash,
                    request.getDonorWalletAddress(),
                    request.getCampaignWalletAddress(),
                    request.getAmount());
//
//            // 🔧 스마트 컨트랙트를 통한 기부 검증
//            // verifyTransaction 메서드가 내부적으로 올바른 검증을 수행하도록 수정
//            boolean isValidTransaction = blockchainService.verifyTransaction(
//                    transactionHash,
//                    request.getDonorWalletAddress(),
//                    request.getCampaignWalletAddress(), // 수혜자 주소 (참고용)
//                    request.getAmount() //이더 단위 금액
//            );
//
//            log.info("✅ 블록체인 검증 결과: {}", isValidTransaction);
//
//            if (!isValidTransaction) {
//                log.error("❌ 블록체인 검증 실패");
//                return ResponseEntity.status(ErrorStatus.DONATION_INVALID_TRANSACTION.getHttpStatus())
//                        .body(ApiResponse.onFailure(
//                                ErrorStatus.DONATION_INVALID_TRANSACTION.getCode(),
//                                ErrorStatus.DONATION_INVALID_TRANSACTION.getMessage(),
//                                null));
//            }

            //💾 기부 기록 저장
            log.info("💾 기부 기록 저장 시작");
            Donation savedDonation = donationService.recordDonation(
                    transactionHash,
                    request.getDonorWalletAddress(),
                    request.getCampaignWalletAddress(),
                    request.getAmount(),
                    request.getCampaignId(),
                    request.getUserId(),
                    request.getMessage()
            );

            log.info("✅ 기부 기록 저장 성공 - ID: {}", savedDonation.getDId());
            log.info("📄 저장된 데이터: {}", savedDonation);

            return ResponseEntity.ok(ApiResponse.of(SuccessStatus.DONATION_RECORDED, null));

        } catch (IllegalArgumentException e) {
            log.error("❌ 기부 기록 중 데이터 오류: ", e);
            if (e.getMessage().contains("학생")) {
                return ResponseEntity.status(ErrorStatus.STUDENT_USER_NOT_FOUND.getHttpStatus())
                        .body(ApiResponse.onFailure(
                                ErrorStatus.STUDENT_USER_NOT_FOUND.getCode(),
                                ErrorStatus.STUDENT_USER_NOT_FOUND.getMessage(),
                                null));
            } else if (e.getMessage().contains("캠페인")) {
                return ResponseEntity.status(ErrorStatus.CAMPAIGN_NOT_FOUND.getHttpStatus())
                        .body(ApiResponse.onFailure(
                                ErrorStatus.CAMPAIGN_NOT_FOUND.getCode(),
                                ErrorStatus.CAMPAIGN_NOT_FOUND.getMessage(),
                                null));
            }
            return ResponseEntity.status(ErrorStatus._BAD_REQUEST.getHttpStatus())
                    .body(ApiResponse.onFailure(
                            ErrorStatus._BAD_REQUEST.getCode(),
                            e.getMessage(),
                            null));
        } catch (Exception e) {
            log.error("❌ 기부 기록 중 예상치 못한 오류 발생: ", e);
            log.error("스택 트레이스: ", e);
            return ResponseEntity.status(ErrorStatus.DONATION_RECORD_FAILED.getHttpStatus())
                    .body(ApiResponse.onFailure(
                            ErrorStatus.DONATION_RECORD_FAILED.getCode(),
                            ErrorStatus.DONATION_RECORD_FAILED.getMessage() + ": " + e.getMessage(),
                            null));
        }
    }

    // 기부 기록 조회 엔드포인트 추가 (디버깅용)
    @GetMapping("/list")
    public ResponseEntity<?> getDonationList() {
        try {
            log.info("📋 기부 기록 목록 조회");
            // 여기서 donationService에 getDonationList() 메서드가 있다고 가정
            // 없다면 간단히 만들어야 합니다
            return ResponseEntity.ok("기부 기록 목록 조회 기능은 DonationService에 구현이 필요합니다.");
        } catch (Exception e) {
            log.error("❌ 기부 기록 목록 조회 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "기부 기록 조회 실패: " + e.getMessage()));
        }
    }

    // 특정 트랜잭션 해시로 기부 기록 조회 (디버깅용)
    @GetMapping("/transaction/{hash}")
    public ResponseEntity<?> getDonationByHash(@PathVariable("hash") String transactionHash) {
        try {
            log.info("트랜잭션 해시로 기부 기록 조회: {}", transactionHash);
            boolean exists = donationService.isDuplicateTransaction(transactionHash);

            Map<String, Object> result = new HashMap<>();
            result.put("transactionHash", transactionHash);
            result.put("exists", exists);
            result.put("message", exists ? "기부 기록이 존재합니다" : "기부 기록이 존재하지 않습니다");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("❌ 트랜잭션 해시 조회 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "조회 실패: " + e.getMessage()));
        }
    }

    //patch -> 부분 수정
    @PatchMapping("/status")
    public ResponseEntity<?> updateDonationStatus(
            @RequestBody @Valid DonationStatusUpdateDTO request
    ) {
        try {
            String txHash = request.getTransactionHash();
            String newStatus = request.getStatus();

            if (txHash == null || txHash.trim().isEmpty() || newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.status(ErrorStatus.DONATION_MISSING_REQUIRED_FIELDS.getHttpStatus())
                        .body(ApiResponse.onFailure(
                                ErrorStatus.DONATION_MISSING_REQUIRED_FIELDS.getCode(),
                                ErrorStatus.DONATION_MISSING_REQUIRED_FIELDS.getMessage(),
                                null));
            }
            DonationStatus statusEnum;

            try {
                statusEnum = DonationStatus.valueOf(newStatus.toUpperCase()); // 문자열 -> enum
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(ErrorStatus.DONATION_INVALID_STATUS.getHttpStatus())
                        .body(ApiResponse.onFailure(
                                ErrorStatus.DONATION_INVALID_STATUS.getCode(),
                                ErrorStatus.DONATION_INVALID_STATUS.getMessage() + ": " + newStatus,
                                null));
            }

            //상태 업데이트
            Donation updated = donationService.updatedDonationStatus(txHash, statusEnum);

            return ResponseEntity.ok(ApiResponse.of(SuccessStatus.DONATION_STATUS_UPDATED, null));
        } catch (DataNotFoundException e) {
            log.error("기부 기록을 찾을 수 없음: ", e);
            return ResponseEntity.status(ErrorStatus.DONATION_NOT_FOUND.getHttpStatus())
                    .body(ApiResponse.onFailure(
                            ErrorStatus.DONATION_NOT_FOUND.getCode(),
                            ErrorStatus.DONATION_NOT_FOUND.getMessage(),
                            null));
        } catch (Exception e) {
            log.error("상태 업데이트 중 오류 발생: ", e);
            return ResponseEntity.status(ErrorStatus.DONATION_STATUS_UPDATE_FAILED.getHttpStatus())
                    .body(ApiResponse.onFailure(
                            ErrorStatus.DONATION_STATUS_UPDATE_FAILED.getCode(),
                            ErrorStatus.DONATION_STATUS_UPDATE_FAILED.getMessage(),
                            null));
        }
    }

    //수혜자 잔액 조회
    @GetMapping("/balance/{address}")
    public ResponseEntity<Map<String, String>> getBalance(@PathVariable("address") String address) {
        try {
            BigInteger balance = blockchainService.getBalance(address);
            BigDecimal ethbalance = new BigDecimal(balance).divide(BigDecimal.TEN.pow(18));

            Map<String, String> response = new HashMap<>();
            response.put("address", address);
            response.put("balance", ethbalance.toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "잔액 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 토큰으로 기부 nft 저장


}