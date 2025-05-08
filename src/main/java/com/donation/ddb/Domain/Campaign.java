package com.donation.ddb.Domain;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "c_image_url", nullable = false, length = 100)
    private String cImageUrl;

    @Column(name = "c_description", nullable = false, length = 5000)
    private String cDescription;

    @Column(name = "c_goal", nullable = false)
    private int cGoal;

    @Column(name = "c_current_amount", nullable = false)
    @Builder.Default
    private int cCurrentAmount = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "c_category", nullable = false)
    private CampaignCategory cCategory = CampaignCategory.NAME1;

    // default value = 0
    @Column(name = "donate_count", nullable = false)
    @Builder.Default
    private long donateCount = 0;

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
    private CampaignStatusFlag cStatusFlag = CampaignStatusFlag.NOT_STARTED;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignPlan> campaignPlanList = new ArrayList<>();

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignSpending> campaignSpendingList = new ArrayList<>();

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
