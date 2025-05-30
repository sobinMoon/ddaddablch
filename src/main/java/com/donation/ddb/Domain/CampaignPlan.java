package com.donation.ddb.Domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CampaignPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cp_id")
    private Long cpId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id")
    private Campaign campaign;

    @Column(name = "cp_title", nullable = false, length = 100)
    private String cpTitle;

    @Column(name = "cp_amount", nullable = false)
    private Integer cpAmount;

}
