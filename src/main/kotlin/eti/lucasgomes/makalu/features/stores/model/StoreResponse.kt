package eti.lucasgomes.makalu.features.stores.model

import com.fasterxml.jackson.annotation.JsonProperty

data class StoreResponse(
    val id: Long,
    val name: String,
    val categories: List<CategoryResponse>,

    @JsonProperty("logo_image_id")
    val logoImageId: Long?,

    @JsonProperty("cover_image_id")
    val coverImageId: Long?
)