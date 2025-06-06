package com.donation.ddb.Domain.Enums;

public enum CampaignStatusFlag {
    // 시작전, 진행중, 종료
    FUNDRAISING(0),
    FUNDED(1),
    IN_PROGRESS(2),
    COMPLETED(3);

    private final int order;

    CampaignStatusFlag(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public static boolean isForwardTransition(CampaignStatusFlag current, CampaignStatusFlag next) {
        return next.getOrder() > current.getOrder();
    }
}
