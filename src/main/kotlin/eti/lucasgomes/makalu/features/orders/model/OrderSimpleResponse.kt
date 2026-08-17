package eti.lucasgomes.makalu.features.orders.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class OrderSimpleResponse(
    val id: Long,
    val status: OrderStatus,

    @JsonProperty("order_number")
    val orderNumber: String,

    @JsonProperty("store_name")
    val storeName: String,

    @JsonProperty("store_image_id")
    val storeImageId: Long?,

    @JsonProperty("delivery_address_line")
    val deliveryAddressLine: String,

    @JsonProperty("items_count")
    val itemsCount: Int,

    val total: Total,

    @JsonProperty("created_at")
    val createdAt: String,

    @JsonProperty("updated_at")
    val updatedAt: String
) {
    data class Total(
        @JsonProperty("delivery_fee")
        val deliveryFee: BigDecimal,
        val total: BigDecimal
    )
}