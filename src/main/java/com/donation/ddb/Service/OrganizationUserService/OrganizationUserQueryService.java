package com.donation.ddb.Service.OrganizationUserService;

import com.donation.ddb.Domain.OrganizationUser;
import com.donation.ddb.Dto.Response.OrganizationResponse;

import java.util.Locale;

public interface OrganizationUserQueryService {
    OrganizationResponse.OrganizationDetailDto convertToDetailDto(OrganizationUser organizationUser);
}
