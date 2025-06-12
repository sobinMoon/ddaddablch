package com.donation.ddb.Repository.CampaignCommentRepostory;

import com.donation.ddb.Domain.CampaignComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CampaignCommentRepository extends JpaRepository<CampaignComment, Long> {
    List<CampaignComment> findByCampaign_cId(Long cId, Pageable pageable);

    Optional<CampaignComment> findByCcId(Long ccId);

    Long countByCampaign_cId(Long cId);

//    CampaignComment save(CampaignComment campaignComment);
}
