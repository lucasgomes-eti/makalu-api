package eti.lucasgomes.makalu.shared.validations

import eti.lucasgomes.makalu.features.auth.AuthService
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class EmailAvailableValidator(val authService: AuthService) : ConstraintValidator<EmailAvailable, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value.isNullOrBlank()) {
            return false
        }

        return authService.isEmailAvailable(value.trim())
    }
}