package eti.lucasgomes.makalu.features.orders

import kotlin.time.Clock
import kotlin.time.Instant

data class OrderEntity(
    val id: Long,
    val userId: Long,
    val storeId: Long,
    val items: List<Item>,
    val createdAt: Instant = Clock.System.now(),
) {
    data class Item(
        val menuItemId: Long,
        val name: String,
        val price: String,
        val configurations: List<Configuration>
    ) {
        data class Configuration(
            val name: String,
            val value: ConfigurationValue
        ) {
            sealed interface ConfigurationValue {
                data class SingleChoice(val value: String) : ConfigurationValue
                data class MultipleChoice(val values: List<String>) : ConfigurationValue
                data class Quantity(val value: Int) : ConfigurationValue
            }
        }
    }
}