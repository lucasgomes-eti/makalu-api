package eti.lucasgomes.makalu

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenPair(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("refresh_token")
    val refreshToken: String
)
