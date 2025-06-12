package com.donation.ddb.Repository.CampaignUpdateRepository;

import com.donation.ddb.Domain.CampaignUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignUpdateRepository extends JpaRepository<CampaignUpdate, Long>, CampaignUpdateRepositoryCustom {
}
