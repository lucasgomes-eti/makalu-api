package eti.lucasgomes.makalu.features.auth.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class RefreshRequest(
    @JsonProperty("refresh_token")
    @field:NotBlank
    val refreshToken: String
)