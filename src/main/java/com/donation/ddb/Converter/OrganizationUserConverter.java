package com.donation.ddb.Converter;

import com.donation.ddb.Domain.OrganizationUser;
import com.donation.ddb.Dto.Request.OrgSignUpForm;

public class OrganizationUserConverter {
    public static OrganizationUser SignUpToOrganizationUser (OrgSignUpForm orgSignUpForm) {
        return OrganizationUser.builder()
                .oEmail(orgSignUpForm.getEmail())
                .oName(orgSignUpForm.getName())
                .oPassword(orgSignUpForm.getPassword())
                .oDescription(orgSignUpForm.getDescription())
                .oProfileImage(orgSignUpForm.getProfileImage())
                .build();
    }
}
