package com.donation.ddb.validation;

import com.donation.ddb.Service.CampaignService.CampaignQueryService;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CampaignExistValidator implements ConstraintValidator<ExistCampaign, Long> {

    private final CampaignQueryService campaignQueryService;

    @Override
    public void initialize(ExistCampaign constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, jakarta.validation.ConstraintValidatorContext context) {
        boolean isValid = campaignQueryService.existsBycId(value);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.CAMPAIGN_NOT_FOUND.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
