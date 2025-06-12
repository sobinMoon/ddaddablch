package com.donation.ddb.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Slf4j
public class StudentUser {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long sId;

    @Column(nullable = false,length=20)
    private String sName;

    @Column(nullable = false,length=100,unique=true)
    private String sNickname;


    //비밀번호는 저장전에 BCrypt 등의 해시 알고리즘 사용하기
    @Column(nullable = false,length=100)
    private String sPassword;

//    @Column(unique=true)
//    private String sWalletAddress;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WalletAuthStatus sWalletAuthStatus=WalletAuthStatus.NONE;

    @Column(nullable = false,unique=true)
    @Email(message="올바른 이메일 형식이어야 합니다.")
    private String sEmail;

    @Column(nullable = false)
    @Builder.Default
    private Boolean sIsActive=true; //기본값 설정

    @Column(length=500)
    private String sProfileImage;

    @CreatedDate
    @Column(nullable=false,updatable=false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable=false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Role role; //ROLE_STUDENT


    @OneToMany(mappedBy = "studentUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<StudentNFT> nftList;

    //여러 지갑 주소 (JSON 배열로 저장)
    @Column(columnDefinition="TEXT")
    private String walletAddresses; // JSON: ["0x123...", "0x456..."]

    @OneToMany(mappedBy = "studentUser", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Post> postList;

    @OneToMany(mappedBy = "studentUser", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PostComment> postCommentList;

    @OneToMany(mappedBy = "studentUser", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PostLike> postLikeList;

    @OneToMany(mappedBy = "studentUser", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PostCommentLike> postCommentLikeList;

    @OneToMany(mappedBy="studentUser",cascade=CascadeType.ALL,orphanRemoval = true)
    private List<CampaignComment> campaignCommentList;

    // StudentUser.java
    @OneToMany(mappedBy = "studentUser")
    @JsonIgnore  // 이 한 줄만 추가
    private List<Donation> donationList;

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
