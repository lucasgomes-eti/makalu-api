package eti.lucasgomes.makalu.features.stores.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class StoreResponse(
    val id: Long,
    val name: String,
    val categories: List<CategoryResponse>,

    @JsonProperty("logo_image_id")
    val logoImageId: Long?,

    @JsonProperty("cover_image_id")
    val coverImageId: Long?,

    val longitude: Double,
    val latitude: Double,

    @JsonProperty("delivery_fee")
    val deliveryFee: BigDecimal
)