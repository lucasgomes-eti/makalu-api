package eti.lucasgomes.makalu.features.auth.model

import com.fasterxml.jackson.annotation.JsonProperty
import eti.lucasgomes.makalu.shared.REGEX_PASSWORD
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 120)
    val name: String,

    @field:Email
    val email: String,

    @JsonProperty("phone_number")
    @field:Size(min = 1, max = 15)
    val phoneNumber: String,

    @field:Pattern(regexp = REGEX_PASSWORD, message = "Password must have letters and numbers.")
    @field:Size(min = 8, max = 25)
    val password: String
)