package eti.lucasgomes.makalu.features.address

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class AddressRequest(
    @field:NotBlank
    @JsonProperty("zip_code")
    val zipCode: String,

    @field:NotBlank
    val street: String,

    val number: String?,
    val complement: String?,

    val longitude: Double,
    val latitude: Double
)