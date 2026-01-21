package eti.lucasgomes.makalu.features.auth.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenPairResponse(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("refresh_token")
    val refreshToken: String
)