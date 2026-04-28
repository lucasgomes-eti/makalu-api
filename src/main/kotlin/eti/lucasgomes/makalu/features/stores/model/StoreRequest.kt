package eti.lucasgomes.makalu.features.stores.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class StoreRequest(

    @field:NotBlank
    val name: String?,

    @JsonProperty("categories_ids")
    @field:NotEmpty
    val categoriesIds: List<Long>?,

    @field:NotNull
    val longitude: Double?,

    @field:NotNull
    val latitude: Double?,

    @JsonProperty("logo_image_id")
    val logoImageId: Long?,

    @JsonProperty("cover_image_id")
    val coverImageId: Long?,

    @JsonProperty("delivery_fee")
    @field:NotNull
    @field:DecimalMin(value = "0.0", inclusive = false)
    val deliveryFee: BigDecimal?
)