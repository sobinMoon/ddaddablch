package com.donation.ddb.apiPayload.exception.handler;

import com.donation.ddb.apiPayload.code.BaseErrorCode;
import com.donation.ddb.apiPayload.exception.GeneralException;

public class OrganizationUserHandler extends GeneralException {
    public OrganizationUserHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
