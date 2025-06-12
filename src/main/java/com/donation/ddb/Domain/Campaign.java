package com.donation.ddb.Domain;

import com.donation.ddb.Domain.Enums.CampaignCategory;
import com.donation.ddb.Domain.Enums.CampaignStatusFlag;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Campaign extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_id")
    private Long cId;

    @Column(name = "c_name", nullable = false, length = 50)
    private String cName;

    @Column(name = "c_image_url", nullable = false, length = 500)
    private String cImageUrl;

    @Column(name = "c_description", nullable = false, length = 5000)
    private String cDescription;

    @Column(name = "c_goal", nullable = false)
    private Integer cGoal;

    @Column(name = "c_current_amount", nullable = false, precision = 38, scale = 18)
    //전체 자리수, 소수점 이하 자리수
    @Builder.Default
    private BigDecimal cCurrentAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "c_category", nullable = false)
    private CampaignCategory cCategory = CampaignCategory.아동청소년;

    @OneToOne(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CampaignUpdate campaignUpdate;
    // default value = 0
    @Column(name = "donate_count", nullable = false)
    @Builder.Default
    private Long donateCount = 0L;

    @Column(name = "donate_start", nullable = false)
    private LocalDate donateStart;

    @Column(name = "donate_end", nullable = false)
    private LocalDate donateEnd;

    @Column(name = "bussiness_start", nullable = false)
    private LocalDate businessStart;

    @Column(name = "bussiness_end", nullable = false)
    private LocalDate businessEnd;

    @Column(name = "c_status_flag", nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CampaignStatusFlag cStatusFlag = CampaignStatusFlag.FUNDRAISING;

    @Column(name = "c_wallet_address", nullable = false)
    private String cWalletAddress;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // 이 한 줄만 추가
    private List<CampaignPlan> campaignPlanList = new ArrayList<>();

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignSpending> campaignSpendingList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "o_id")
    private OrganizationUser organizationUser;

    public void addDonateCount(){
        this.donateCount = this.donateCount + 1;
    }

    public void addCurrentAmount(BigDecimal amount){
        this.cCurrentAmount = this.cCurrentAmount.add(amount);
    }

    @Override
    public String toString() {
        return "Campaign{" +
                "cId=" + cId +
                ", cName='" + cName + '\'' +
                ", cImageUrl='" + cImageUrl + '\'' +
                ", cDescription='" + cDescription + '\'' +
                ", cGoal=" + cGoal +
                ", cCurrentAmount=" + cCurrentAmount +
                ", cCategory=" + cCategory +
                ", donateCount=" + donateCount +
                ", donateStart=" + donateStart +
                ", donateEnd=" + donateEnd +
                ", businessStart=" + businessStart +
                ", businessEnd=" + businessEnd +
                ", cStatusFlag=" + cStatusFlag +
                '}';
    }

}
