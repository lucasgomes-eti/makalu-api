package eti.lucasgomes.makalu.features.menu.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class MenuItemResponse(
    val id: Long,

    @JsonProperty("store_id")
    val storeId: Long,

    val category: String,
    val name: String,
    val price: BigDecimal,
    val ingredients: String?,
    val configurations: List<Configuration>,

    @JsonProperty("image_id")
    val imageId: Long?
) {
    data class Configuration(
        val name: String,
        val type: Type,
        val options: List<String>
    ) {
        enum class Type { SINGLE_CHOICE, MULTIPLE_CHOICE, QUANTITY }
    }
}