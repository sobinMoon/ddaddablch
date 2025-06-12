package com.donation.ddb.Repository.CampaignRepository;

import com.donation.ddb.Domain.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long>, CampaignRepositoryCustom {
    // Custom query methods can be defined here if needed
    // For example, find campaigns by status or category
    // List<Campaign> findByStatus(CampaignStatusFlag status);
    // List<Campaign> findByCategory(CampaignCategory category);

    // optional은 단일 엔티티 조회 시 사용, null일 수 있음
//    List<Campaign> findCampaignBycStatusFlag(CampaignStatusFlag cStatusFlag);
    Campaign findBycName(String cname);
    Campaign findBycId(Long cId);
    Optional<Campaign> findBycWalletAddress(String walletAddress);

    Boolean existsBycId(Long cId);
}
