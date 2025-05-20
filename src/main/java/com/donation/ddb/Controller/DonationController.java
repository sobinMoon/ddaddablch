package com.donation.ddb.Controller;

import com.donation.ddb.Dto.Request.DonationRequestDTO;
import com.donation.ddb.Repository.OrganizationUserRepository;
import com.donation.ddb.Service.BlockchainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

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
public class DonationController {
    private final BlockchainService blockchainService;
    private final OrganizationUserRepository organizationUserRepository;

    //기부
    @PostMapping("/donate")
    public ResponseEntity<Map<String, String>>
    donate(@RequestBody DonationRequestDTO request) {
        try {
            //ETH를 wei로 변환 (1eth=10^18 wei)
            BigInteger weiAmount = Convert.toWei(
                    request.getAmount().toString(),
                    Convert.Unit.ETHER
            ).toBigInteger();

            // 블록체인에 트랜잭션 전송
            CompletableFuture<TransactionReceipt> future =
                    blockchainService.donate(request.getBeneficiary(), weiAmount);
            // 결과 반환 (트랜잭션 해시)
            Map<String, String> response = new HashMap<>();
            response.put("status", "Transactio 보내짐.");
            response.put("transactionHash", future.join().getTransactionHash());

            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    //수혜자 잔액 조회
    @GetMapping("/balance/{address}")
    public ResponseEntity<Map<String,String>> getBalance(@PathVariable("address") String address) {
        try {
            if((organizationUserRepository.findByoWalletAddress(address)).isEmpty()){
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "존재하지 않는 수혜자 주소입니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            BigInteger balance = blockchainService.getBalance(address);
            BigDecimal ethbalance=new BigDecimal(balance).divide(BigDecimal.TEN.pow(18));


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

//    //수혜자가 기부금 인출
//    @PostMapping("/withdraw/{beneficiaryAddress}")
//    public ResponseEntity<?> withdrawFunds(@PathVariable("beneficiaryAddress") String beneficiaryAddress) {
//        try {
//            String txHash=blockchainService.withdrawFunds(beneficiaryAddress);
//
//            Map<String, Object> result = new HashMap<>();
//            result.put("transactionHash", txHash);
//            result.put("status", "success");
//
//            return ResponseEntity.ok(result);
//        } catch(Exception e){
//            return ResponseEntity.badRequest().body("인출 실패: " + e.getMessage());
//
//        }
//    }

}
