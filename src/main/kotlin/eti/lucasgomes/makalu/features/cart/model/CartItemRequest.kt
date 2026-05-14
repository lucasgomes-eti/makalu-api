package eti.lucasgomes.makalu.features.cart.model

import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CartItemRequest(
    @field:NotNull
    @field:Valid
    val configurations: List<Configuration>?
) {
    data class Configuration(
        @field:NotBlank
        val name: String?,

        @field:NotNull
        @field:Min(1)
        val quantity: Int?,

        @field:NotNull
        @field:DecimalMin(value = "0.0")
        val price: BigDecimal?
    )
}
