package com.donation.ddb.Service.OrganizationUserService;

import com.donation.ddb.Domain.OrganizationUser;
import com.donation.ddb.Dto.Response.OrganizationResponse;
import com.donation.ddb.Repository.OrganizationUserRepository.OrganizationUserRepository;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.handler.OrganizationUserHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationUserQueryServiceImpl implements OrganizationUserQueryService {

    private final OrganizationUserRepository organizationUserRepository;

    OrganizationUser findByoId(Long oId) {
        return organizationUserRepository.findByoId(oId)
                .orElseThrow(() -> new OrganizationUserHandler(ErrorStatus.ORGANIZATION_USER_NOT_FOUND));
    }

    public OrganizationResponse.OrganizationDetailDto convertToDetailDto(OrganizationUser user) {
        return OrganizationResponse.OrganizationDetailDto.builder()
                .oId(user.getOId())
                .oName(user.getOName())
                .oProfileImage(user.getOProfileImage())
                .oDescription(user.getODescription())
                .build();
    }
}
