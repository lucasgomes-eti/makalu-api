package eti.lucasgomes.makalu.features.auth.model

import com.fasterxml.jackson.annotation.JsonProperty

data class RefreshRequest(
    @JsonProperty("refresh_token")
    val refreshToken: String
)