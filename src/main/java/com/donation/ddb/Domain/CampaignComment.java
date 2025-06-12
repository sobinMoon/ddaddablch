package com.donation.ddb.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CampaignComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cc_id")
    private Long ccId;

    @Column(name = "cc_content", nullable = false, length = 500)
    private String ccContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id", nullable = false)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private StudentUser studentUser;

    @OneToMany(mappedBy = "campaignComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignCommentLike> campaignCommentLikeList = new ArrayList<>();
}
