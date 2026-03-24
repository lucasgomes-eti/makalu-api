package eti.lucasgomes.makalu.features.stores.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class StoreRequest(

    @field:NotBlank
    val name: String,

    @JsonProperty("categories_ids")
    @field:NotEmpty
    val categoriesIds: List<Long>,

    @field:NotNull
    val longitude: Double?,

    @field:NotNull
    val latitude: Double?
)