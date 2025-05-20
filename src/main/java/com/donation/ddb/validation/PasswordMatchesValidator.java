package com.donation.ddb.validation;

import com.donation.ddb.Dto.Request.OrgSignUpForm;
import com.donation.ddb.validation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, OrgSignUpForm> {

    @Override
    public boolean isValid(OrgSignUpForm form, ConstraintValidatorContext context) {
        if (form.getPassword() == null || form.getConfirmPassword() == null) {
            return false;
        }
        return form.getPassword().equals(form.getConfirmPassword());
    }
}