package com.donation.ddb.Domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CampaignUpdate extends BaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "cu_id")
    private Long cuId;

    @Column(nullable = false, length = 100)
    private String cuTitle;

    @Column(nullable = false, length = 5000)
    private String cuContent;

    @Column(nullable = false, length = 200)
    private String cuImageUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id", nullable = false)
    private Campaign campaign;
}
