package com.donation.ddb.Repository.CampaignUpdateRepository;

import com.donation.ddb.Domain.CampaignUpdate;
import com.donation.ddb.Repository.projection.CampaignWithUpdate;

import java.util.List;

public interface CampaignUpdateRepositoryCustom {
     List<CampaignWithUpdate> findLatestUpdates();
}
