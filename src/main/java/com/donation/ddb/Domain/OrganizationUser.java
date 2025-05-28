package com.donation.ddb.Domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class OrganizationUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long oId;

    @Column(nullable = false,length=20)
    private String oName;

    @Column(nullable = false,unique=true)
    private String oEmail;

    @Column(nullable = false,length=100)
    private String oPassword;

//    @Column(unique=true)
//    private String oWalletAddress; //추 후 삭제 가능

    @Column(columnDefinition = "TEXT")
    private String walletAddresses; //JSON=["0xabc...","0xbdf..."]

    @Column(unique=true)
    private String oBusinessNumber;

    @Column(length=100)
    private String oDescription;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WalletAuthStatus oWalletAuthStatus=WalletAuthStatus.NONE;

    @Column(nullable = false)
    @Builder.Default
    private Boolean oIsActive=true; //기본값 설정

    @Column(length=255)
    private String oProfileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.ROLE_ORGANIZATION; //ROLE_ORGANIZATION

    // 수정: orphanRemoval 추가
    @OneToMany(mappedBy = "organizationUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Campaign> campaigns = new ArrayList<>();


    //지갑 관리 메소드
    //지갑 주소 목록 반환
    public List<String> getWalletList(){
        if(walletAddresses==null || walletAddresses.isEmpty()){
            return new ArrayList<>();
        }
        try{
            ObjectMapper mapper=new ObjectMapper();
            return mapper.readValue(walletAddresses, new TypeReference<List<String>>() {});
        } catch(Exception e){
            log.warn("지갑 주소 파싱 실패 : {}",e.getMessage());
            return new ArrayList<>();
        }
    }

    //지갑 주소 추가
    public void addWallet(String walletAddress) {
        List<String> wallets = getWalletList();
        String lowerWallet = walletAddress.toLowerCase();

        if (!wallets.contains(lowerWallet)) {
            wallets.add(lowerWallet);
            try {
                ObjectMapper mapper = new ObjectMapper();
                this.walletAddresses = mapper.writeValueAsString(wallets);
            } catch (Exception e) {
                throw new RuntimeException("지갑 주소 저장 실패: " + e.getMessage(), e);
            }
        }
    }

    //이 지갑주소가 내가 인증한 지갑주소인지
    public boolean hasWallet(String walletAddress){
        if(walletAddress==null) return false;
        return getWalletList().contains(walletAddress.toLowerCase());
    }

}
