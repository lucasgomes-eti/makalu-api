package eti.lucasgomes.makalu.features.stores.model

import jakarta.validation.constraints.NotBlank

data class CategoryRequest(
    @field:NotBlank
    val description: String
)