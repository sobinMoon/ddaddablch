package com.donation.ddb.Repository.CampaignSpendingRepository;

import com.donation.ddb.Domain.CampaignSpending;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignSpendingRepository extends JpaRepository<CampaignSpending, Long> {
    List<CampaignSpending> findByCampaign_cId(Long cId);
}
