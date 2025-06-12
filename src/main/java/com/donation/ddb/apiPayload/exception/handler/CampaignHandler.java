package com.donation.ddb.apiPayload.exception.handler;

import com.donation.ddb.apiPayload.code.BaseErrorCode;
import com.donation.ddb.apiPayload.exception.GeneralException;

public class CampaignHandler extends GeneralException {
    public CampaignHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
