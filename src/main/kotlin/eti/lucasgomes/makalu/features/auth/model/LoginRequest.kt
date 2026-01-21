package eti.lucasgomes.makalu.features.auth.model

import eti.lucasgomes.makalu.shared.REGEX_PASSWORD
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

data class LoginRequest(
    @field:Email
    val email: String,

    @field:Pattern(regexp = REGEX_PASSWORD, message = "Password must have letters and numbers.")
    val password: String
)