package eti.lucasgomes.makalu.features.orders

import com.fasterxml.jackson.annotation.JsonProperty
import eti.lucasgomes.makalu.features.orders.model.OrderStatus
import java.math.BigDecimal

data class OrderResponse(
    val id: Long,
    val status: OrderStatus,

    @JsonProperty("order_number")
    val orderNumber: String,

    @JsonProperty("store_name")
    val storeName: String,

    @JsonProperty("delivery_address_line")
    val deliveryAddressLine: String,

    val items: List<OrderItemResponse>,
    val total: Total,

    @JsonProperty("created_at")
    val createdAt: String
) {
    data class OrderItemResponse(
        val id: Long,

        @JsonProperty("menu_item_id")
        val menuItemId: Long,

        @JsonProperty("image_id")
        val imageId: Long?,

        val name: String,
        val price: BigDecimal,
        val notes: String?,
        val configurations: List<Configuration>
    ) {
        data class Configuration(
            val id: Long,
            val name: String,
            val options: List<Option>
        ) {
            data class Option(
                val id: Long,
                val name: String,

                @JsonProperty("additional_price")
                val additionalPrice: BigDecimal,

                val quantity: Int
            )
        }
    }

    data class Total(
        @JsonProperty("delivery_fee")
        val deliveryFee: BigDecimal,
        val total: BigDecimal
    )
}
