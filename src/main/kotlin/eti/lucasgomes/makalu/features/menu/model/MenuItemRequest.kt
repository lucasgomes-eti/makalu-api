package eti.lucasgomes.makalu.features.menu.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class MenuItemRequest(
    @field:NotBlank
    val name: String?,

    @field:NotBlank
    val category: String?,

    @field:NotNull
    @field:DecimalMin(value = "0.0", inclusive = false)
    val price: BigDecimal?,

    val ingredients: String?,

    val configurations: List<Configuration>?
) {
    data class Configuration(
        @field:NotBlank
        val name: String?,

        @field:NotNull
        val type: Type?,

        @field:NotNull
        val options: List<Option>?
    ) {
        enum class Type { SINGLE_CHOICE, MULTIPLE_CHOICE, QUANTITY }

        data class Option(
            @field:NotBlank
            val name: String?,

            @JsonProperty("additional_price")
            @field:NotNull
            @field:DecimalMin(value = "0.0", inclusive = false)
            val additionalPrice: BigDecimal?
        )
    }
}
