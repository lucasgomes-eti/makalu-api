package eti.lucasgomes.makalu.features.stores.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class StoreRequest(

    @field:NotBlank
    val name: String,

    @JsonProperty("categories_ids")
    @field:NotEmpty
    val categoriesIds: List<Long>,
)