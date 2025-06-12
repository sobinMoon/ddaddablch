package com.donation.ddb.apiPayload.exception.handler;

import com.donation.ddb.apiPayload.code.BaseErrorCode;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.GeneralException;

public class PostHandler extends GeneralException {
    public PostHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
