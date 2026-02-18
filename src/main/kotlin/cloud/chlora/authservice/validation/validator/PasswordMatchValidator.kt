package cloud.chlora.authservice.validation.validator

import cloud.chlora.authservice.dto.request.RegisterRequest
import cloud.chlora.authservice.validation.annotation.PasswordMatch
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PasswordMatchValidator : ConstraintValidator<PasswordMatch, RegisterRequest> {

    override fun isValid(value: RegisterRequest?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true
        return value.password == value.confirmPassword
    }
}