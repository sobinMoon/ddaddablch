package com.donation.ddb.Domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CampaignSpending {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cs_id")
    private Long csId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id")
    private Campaign campaign;

    @Column(name = "cs_title", nullable = false, length = 100)
    private String csTitle;

    @Column(name = "cs_amount", nullable = false)
    private int csAmount;
}
