package eti.lucasgomes.makalu.features.address

import com.fasterxml.jackson.annotation.JsonProperty

data class AddressResponse(
    val id: Long,

    @JsonProperty("zip_code")
    val zipCode: String,

    val street: String,
    val number: String?,
    val complement: String?,
    val longitude: Double,
    val latitude: Double
)
