package eti.lucasgomes.makalu.shared.validations

import eti.lucasgomes.makalu.features.auth.AuthService
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PhoneNumberAvailableValidator(val authService: AuthService) : ConstraintValidator<PhoneNumberAvailable, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value.isNullOrBlank()) {
            return false
        }

        return authService.isPhoneNumberAvailable(value.trim())
    }
}