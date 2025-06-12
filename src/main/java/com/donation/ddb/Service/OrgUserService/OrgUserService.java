package com.donation.ddb.Service.OrgUserService;


import com.donation.ddb.Domain.*;
import com.donation.ddb.Domain.Exception.DataNotFoundException;
import com.donation.ddb.Domain.Exception.EmailAlreadyExistsException;
import com.donation.ddb.Dto.Request.OrgSignUpForm;
import com.donation.ddb.Repository.OrganizationUserRepository;
import com.donation.ddb.Repository.VerificationTokenRepository;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import com.donation.ddb.apiPayload.exception.GeneralException;
import com.donation.ddb.apiPayload.exception.handler.CampaignHandler;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrgUserService {

    @Autowired
    private final OrganizationUserRepository organizationUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository tokenRepository;

    @Transactional
    public Long createOrg(String name, String email,
                          String password,String confirmPassword,
                          String businessNumber,
                          String description,
                          String profileimage){
        if(StringUtils.isEmpty(password) || !password.equals(confirmPassword)){
            throw new IllegalStateException("비밀번호가 일치하지 않습니다. ");
        }

        OrganizationUser user=new OrganizationUser();

        VerificationToken foundToken=tokenRepository.findByEmail(email)
                .orElseThrow(()-> new DataNotFoundException("이메일 인증을 먼저 해주세요."));

        user.setOName(name);

        if(organizationUserRepository.existsByoEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        user.setOEmail(email);
        user.setOPassword(passwordEncoder.encode(password));


        user.setRole(Role.ROLE_ORGANIZATION);
        user.setOBusinessNumber(businessNumber);
        user.setODescription(description);
        user.setOProfileImage(profileimage);

        OrganizationUser s=organizationUserRepository.save(user);
        Long id=user.getOId();
        return id;
    }

    @Transactional
    public OrganizationUser updateOrgUser(OrgSignUpForm orgSignUpForm, Long orgId) {
        OrganizationUser orgUser = organizationUserRepository.findById(orgId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ORGANIZATION_USER_NOT_FOUND));

        orgUser.setOProfileImage(orgSignUpForm.getProfileImage());

        return orgUser;
    }

}
