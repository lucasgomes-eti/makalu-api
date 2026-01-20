package eti.lucasgomes.makalu

import com.fasterxml.jackson.annotation.JsonAlias

data class CreateAccountRequest(
    val name: String,
    val email: String,
    @JsonAlias("phone_number")
    val phoneNumber: String,
    val password: String
)
