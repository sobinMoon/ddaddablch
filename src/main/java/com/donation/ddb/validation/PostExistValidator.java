package com.donation.ddb.validation;

import com.donation.ddb.Service.PostService.PostQueryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.donation.ddb.apiPayload.code.status.ErrorStatus;

@Component
@RequiredArgsConstructor
public class PostExistValidator implements ConstraintValidator<ExistPost, Long> {

    private final PostQueryService postQueryService;

    @Override
    public void initialize(ExistPost constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        boolean isValid = postQueryService.existsBypId(value);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.POST_NOT_FOUND.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
