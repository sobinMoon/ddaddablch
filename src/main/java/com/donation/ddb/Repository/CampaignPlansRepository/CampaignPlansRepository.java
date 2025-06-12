package com.donation.ddb.Repository.CampaignPlansRepository;

import com.donation.ddb.Domain.CampaignPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignPlansRepository extends JpaRepository<CampaignPlan, Long> {
    List<CampaignPlan> findByCampaign_cId(Long cId);
}
