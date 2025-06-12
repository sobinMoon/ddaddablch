package com.donation.ddb.Service.DonationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.crypto.Credentials;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
public class BlockchainService {

    @Value("${test.mode:false}")
    private boolean testMode;

    private Web3j web3j;
    private Credentials credentials;
    private TransactionManager transactionManager;
    private ContractGasProvider gasProvider;
    private String contractAddress;
    private JsonNode contractAbi;

    // Ganache URL
    @Value("${ethereum.network.url:http://127.0.0.1:7545}")
    private String networkUrl;

    @Value("${ethereum.private.key}")
    private String privateKey;

    @Value("${ethereum.contract.address}")
    private String contractAddressValue;

    @PostConstruct
    public void init() throws IOException {
        // Web3j 인스턴스 생성 - Ganache에 연결
        web3j = Web3j.build(new HttpService(networkUrl));

        try {
            // 연결 확인
            String clientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();
            log.info("Connected to Ethereum client: " + clientVersion);

        } catch (Exception e) {
            log.warn("Error connecting to Ethereum client: " + e.getMessage());
            throw new RuntimeException("Failed to connect to Ethereum client", e);
        }

        // 자격 증명 설정
        credentials = Credentials.create(privateKey); //기부 트랜잭션을 서명할 지갑의 개인키
        //credentials=개인키 기반으로 서명자 주소 생성
        log.info("Using address: " + credentials.getAddress());

        // 트랜잭션 매니저 - Ganache는 낮은 가스 가격을 사용
        transactionManager = new RawTransactionManager(web3j, credentials);
        //rawtransactionManager: 지갑으로 직접 트랜잭션에 서명하고 블록체인에 보낼 수 있는 도구

        // Ganache용 가스 설정 (가스 가격이 낮음)
        gasProvider = new DefaultGasProvider() {
            @Override
            public BigInteger getGasPrice() {
                return BigInteger.valueOf(20_000_000_000L); // 20 Gwei
            }

            @Override
            public BigInteger getGasLimit() {
                return BigInteger.valueOf(6_721_975L); // Ganache 기본값
            }
        };

        // 컨트랙트 주소 설정
        contractAddress = contractAddressValue;

        // JSON 파일에서 ABI 로드
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode contractJson = mapper.readTree(
                    new ClassPathResource("contracts/SimpleDonationPlatform.json").getInputStream()
            );
            contractAbi = contractJson.get("abi");

            // 컨트랙트 정보 출력
            log.info("Contract loaded: " + contractAddress);

        } catch (IOException e) {
            log.warn("Error loading contract JSON: " + e.getMessage());
            throw e;
        }
    }

    //    // 수혜자에게 기부
//    public CompletableFuture<TransactionReceipt> donate(String beneficiaryAddress, BigInteger amount) {
//        log.info("Donating " + amount + " wei to " + beneficiaryAddress);
//
//
//        // donate 함수 인코딩
//        Function function = new Function(
//                "donate",
//                Collections.singletonList(new Address(beneficiaryAddress)),
//                Collections.emptyList()
//        );
//
//        String encodedFunction = FunctionEncoder.encode(function);
//
//        // 트랜잭션 전송 (비동기)
//        try {
//            // 트랜잭션 생성 및 전송
//            CompletableFuture<TransactionReceipt> futureReceipt = new CompletableFuture<>();
//
//            // 비동기 방식으로 트랜잭션 전송
//            web3j.ethSendTransaction(
//                    Transaction.createFunctionCallTransaction(
//                            credentials.getAddress(),   // 보내는 주소
//                            null,                       // nonce (자동)
//                            gasProvider.getGasPrice(),  // 가스 가격
//                            gasProvider.getGasLimit(),  // 가스 한도
//                            contractAddress,            // 계약 주소
//                            amount,                     // 보낼 이더 양
//                            encodedFunction             // 인코딩된 함수
//                    )
//            ).sendAsync().thenAccept(ethSendTransaction -> {
//                if (ethSendTransaction.hasError()) {
//                    // 트랜잭션 전송 오류 처리
//                    String errorMessage = ethSendTransaction.getError().getMessage();
//                    log.error("Transaction error: " + errorMessage);
//                    futureReceipt.completeExceptionally(
//                            new RuntimeException("Transaction error: " + errorMessage));
//                    return;
//                }
//
//                // 트랜잭션 해시 가져오기
//                String txHash = ethSendTransaction.getTransactionHash();
//                log.info("Transaction hash: " + txHash);
//
//                // 트랜잭션 영수증 대기 및 처리
//                try {
//                    TransactionReceipt receipt = waitForTransactionReceipt(txHash);
//                    log.info("Transaction mined, status: " + receipt.getStatus());
//                    futureReceipt.complete(receipt);
//                } catch (Exception e) {
//                    log.warn("Error processing transaction: " + e.getMessage());
//                    futureReceipt.completeExceptionally(
//                            new RuntimeException("Failed to get transaction receipt", e));
//                }
//            }).exceptionally(ex -> {
//                log.error("Transaction send failed: " + ex.getMessage());
//                futureReceipt.completeExceptionally(
//                        new RuntimeException("Transaction send failed", ex));
//                return null;
//            });
//
//            return futureReceipt;
//        } catch (Exception e) {
//            throw new RuntimeException("Transaction preparation failed", e);
//        }
//
//
//    }
    // 트랜잭션 영수증을 기다리는 도우미 메서드
    private TransactionReceipt waitForTransactionReceipt(String transactionHash) throws Exception {
        int attempts = 40; // 최대 시도 횟수
        int sleepDuration = 1000; // 각 시도 사이의 대기 시간(밀리초)

        for (int i = 0; i < attempts; i++) {
            Optional<TransactionReceipt> receiptOptional = web3j.ethGetTransactionReceipt(transactionHash)
                    .send()
                    .getTransactionReceipt();

            if (receiptOptional.isPresent()) {
                return receiptOptional.get();
            }

            // 다음 시도 전에 잠시 대기
            Thread.sleep(sleepDuration);
        }

        throw new RuntimeException("Transaction receipt not available after " + attempts + " attempts");
    }

    // 잔액조회
    public BigInteger getBalance(String address) throws Exception {
        log.info("Checking balance for " + address);
        if (testMode) {
            log.info("테스트 모드: 가상 잔액 반환 - Address: {}", address);
            return Convert.toWei("5.0", Convert.Unit.ETHER).toBigInteger(); // 5 ETH 반환
        }
        Function function = new Function(
                "getBeneficiaryBalance",//호출할 컨트랙트 함수 이름
                Collections.singletonList(new Address(address)), //입력
                Collections.singletonList(new TypeReference<Uint256>() {}) //반환
        );

        String encodedFunction = FunctionEncoder.encode(function);

        // 컨트랙트 함수 호출 (읽기 전용)
        EthCall ethCall = web3j.ethCall( //읽기 전용 함수 호출 -> 블록체인 기록 x
                Transaction.createEthCallTransaction(
                        credentials.getAddress(),
                        contractAddress,
                        encodedFunction
                ),
                DefaultBlockParameterName.LATEST //가장 최근 블록 기준
        ).send();

        if (ethCall.hasError()) {
            throw new RuntimeException("Error calling contract: " + ethCall.getError().getMessage());
        }

        List<Type> result = FunctionReturnDecoder.decode(
                ethCall.getValue(), //hex문자열로 컨트랙트로부터 받음
                function.getOutputParameters() //반환값 타입 정보
        );

        BigInteger balance = ((Uint256) result.get(0)).getValue(); //디코딩된 첫번째값 꺼내기
        log.info("Balance: " + balance + " wei");

        return balance;
    }

    // 트랜잭션 검증 (수정된 버전)
    public boolean verifyTransaction(String transactionHash, String expectedFromAddress,
                                     String expectedBeneficiaryAddress, BigDecimal expectedAmount) {
        // 테스트 모드일 때는 간단한 검증만 수행
        if (testMode) {
            log.info("테스트 모드: 트랜잭션 검증 우회 - Hash: {}", transactionHash);

            // 기본적인 형식 검증만 수행
            if (transactionHash != null && transactionHash.startsWith("0x") && transactionHash.length() == 66) {
                return true;  // 테스트용으로 항상 성공 반환
            }
            return false;
        }

        try {
            log.info("🔍 스마트 컨트랙트 기부 트랜잭션 검증 시작");
            log.info("📋 검증 정보:");
            log.info("  - 트랜잭션 해시: {}", transactionHash);
            log.info("  - 기부자 주소: {}", expectedFromAddress);
            log.info("  - 수혜자 주소: {} (참고용)", expectedBeneficiaryAddress);
            log.info("  - 기부 금액: {} ETH", expectedAmount);
            log.info("  - 컨트랙트 주소: {}", contractAddress);

            // 1. 트랜잭션 영수증 조회
            EthGetTransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();

            if (!transactionReceipt.getTransactionReceipt().isPresent()) {
                log.warn("❌ 트랜잭션을 찾을 수 없습니다: {}", transactionHash);
                return false;
            }

            TransactionReceipt receipt = transactionReceipt.getTransactionReceipt().get();
            log.info("✅ 트랜잭션 영수증 조회 성공");
            log.info("  - Status: {}", receipt.getStatus());
            log.info("  - Gas Used: {}", receipt.getGasUsed());

            // 2. 트랜잭션 성공 여부 확인
            if (!"0x1".equals(receipt.getStatus())) {
                log.warn("❌ 실패한 트랜잭션입니다: {}", transactionHash);
                return false;
            }

            // 3. 실제 트랜잭션 정보 조회
            EthTransaction ethTransaction = web3j.ethGetTransactionByHash(transactionHash).send();

            if (!ethTransaction.getTransaction().isPresent()) {
                log.warn("❌ 트랜잭션 정보를 찾을 수 없습니다: {}", transactionHash);
                return false;
            }

            org.web3j.protocol.core.methods.response.Transaction transaction =
                    ethTransaction.getTransaction().get();

            log.info("✅ 트랜잭션 조회 성공");
            log.info("  - From: {}", transaction.getFrom());
            log.info("  - To: {}", transaction.getTo());
            log.info("  - Value: {} Wei", transaction.getValue());

            // 4. 송신자 주소 검증
            if (!expectedFromAddress.equalsIgnoreCase(transaction.getFrom())) {
                log.warn("❌ 송신자 주소가 일치하지 않습니다. 예상: {}, 실제: {}",
                        expectedFromAddress, transaction.getFrom());
                return false;
            }

            // 🔧 5. 수신자 주소 검증 (스마트 컨트랙트 주소여야 함)
            if (!contractAddress.equalsIgnoreCase(transaction.getTo())) {
                log.warn("❌ 트랜잭션이 올바른 스마트 컨트랙트로 전송되지 않았습니다. 예상: {}, 실제: {}",
                        contractAddress, transaction.getTo());
                return false;
            }

            // 6. 금액 검증
            BigInteger actualAmount = transaction.getValue();
            BigInteger expectedWeiAmount = Convert.toWei(expectedAmount, Convert.Unit.ETHER).toBigIntegerExact();

            log.info("💰 금액 검증:");
            log.info("  - 예상 금액: {} Wei ({} ETH)", expectedWeiAmount, expectedAmount);
            log.info("  - 실제 금액: {} Wei ({} ETH)", actualAmount,
                    Convert.fromWei(new BigDecimal(actualAmount), Convert.Unit.ETHER));

            if (actualAmount.compareTo(expectedWeiAmount) != 0) {
                log.warn("❌ 전송 금액이 일치하지 않습니다. 예상: {} wei, 실제: {} wei",
                        expectedWeiAmount, actualAmount);
                return false;
            }

            log.info("✅ 스마트 컨트랙트 기부 트랜잭션 검증 완료: {}", transactionHash);
            return true;

        } catch (Exception e) {
            log.error("❌ 트랜잭션 검증 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    // Web3j 연결 상태 확인
    public boolean isConnected() {
        try {
            web3j.web3ClientVersion().send();
            return true;
        } catch (Exception e) {
            log.error("Web3j 연결 확인 실패: {}", e.getMessage());
            return false;
        }
    }

    // 현재 가스 가격 조회
    public BigInteger getCurrentGasPrice() throws Exception {
        return web3j.ethGasPrice().send().getGasPrice();
    }

}