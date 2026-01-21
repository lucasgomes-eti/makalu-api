package eti.lucasgomes.makalu.features.profile

import com.fasterxml.jackson.annotation.JsonProperty

data class ProfileResponse(
    val id: Long,
    val name: String,
    val email: String,
    @JsonProperty("phone_number")
    val phoneNumber: String,
)