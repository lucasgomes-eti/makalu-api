package eti.lucasgomes.makalu.features.realtime.model

import com.fasterxml.jackson.annotation.JsonProperty

data class RealtimeCredentialsResponse(
    val url: String,
    val channel: String,
    val key: String,

    @JsonProperty("expires_at")
    val expiresAt: String
)
