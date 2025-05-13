package com.donation.ddb.Domain;

import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.handler.CampaignHandler;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum CampaignCategory {
    ALL,
    아동청소년,
    노인,
    환경,
    동물,
    장애인,
    사회;
}
