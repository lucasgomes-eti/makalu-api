package eti.lucasgomes.makalu

import com.fasterxml.jackson.annotation.JsonProperty

data class RefreshRequest(
    @JsonProperty("refresh_token")
    val refreshToken: String
)
