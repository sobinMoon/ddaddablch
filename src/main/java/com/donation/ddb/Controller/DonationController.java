package com.donation.ddb.Controller;

import com.donation.ddb.Dto.Request.DonationRequestDTO;
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
}
