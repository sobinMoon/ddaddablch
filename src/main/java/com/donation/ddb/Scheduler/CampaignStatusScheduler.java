package com.donation.ddb.Scheduler;

import com.donation.ddb.Service.CampaignService.CampaignCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CampaignStatusScheduler {

    private final CampaignCommandService campaignCommandService;

    @Scheduled(cron = "0 0 0 * * *") // 매일 00시
    public void updateStatusByDate() {
        log.info("[스케줄러] 캠페인 상태 업데이트 시작");
        campaignCommandService.updateStatusByDate();
    }
}
