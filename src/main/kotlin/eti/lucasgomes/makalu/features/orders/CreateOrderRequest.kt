package eti.lucasgomes.makalu.features.orders

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class CreateOrderRequest(
    @field:NotNull
    @JsonProperty("store_id")
    val storeId: Long?,

    @field:NotNull
    @JsonProperty("cart_id")
    val cartId: Long?
)
