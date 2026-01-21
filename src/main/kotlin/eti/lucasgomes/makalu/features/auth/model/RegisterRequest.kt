package eti.lucasgomes.makalu.features.auth.model

import com.fasterxml.jackson.annotation.JsonProperty

data class RegisterRequest(
    val name: String,
    val email: String,
    @JsonProperty("phone_number")
    val phoneNumber: String,
    val password: String
)