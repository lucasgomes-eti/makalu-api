package eti.lucasgomes.makalu.features.cart.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class CartResponse(
    val id: Long,

    @JsonProperty("delivery_address_line")
    val deliveryAddressLine: String?,

    val items: List<CartItemResponse>,
    val total: Total
) {
    data class CartItemResponse(
        val id: Long,

        @JsonProperty("image_id")
        val imageId: Long?,

        val name: String,
        val price: BigDecimal,
        val notes: String?,
        val configurations: List<Configuration>
    ) {
        data class Configuration(
            @JsonProperty("configuration_id")
            val configurationId: Long,
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
