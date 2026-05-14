package eti.lucasgomes.makalu.features.cart.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

data class CartItemRequest(
    @field:NotNull
    @field:Valid
    val configurations: List<Configuration>?
) {
    data class Configuration(
        @field:NotNull
        @JsonProperty("menu_item_configuration_option_id")
        val menuItemConfigurationOptionId: Long?,

        val quantity: Int? = 1
    )
}
