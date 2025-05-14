package com.donation.ddb.apiPayload.exception.handler;

import com.donation.ddb.apiPayload.code.BaseErrorCode;
import com.donation.ddb.apiPayload.exception.GeneralException;

public class CampaignPlanHandler extends GeneralException {
    public CampaignPlanHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
