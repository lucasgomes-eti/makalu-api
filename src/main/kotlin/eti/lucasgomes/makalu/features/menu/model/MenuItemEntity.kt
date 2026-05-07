package eti.lucasgomes.makalu.features.menu.model

import eti.lucasgomes.makalu.features.menu.ConfigurationListConverter
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.math.BigDecimal
import kotlin.time.Clock
import kotlin.time.Instant

@Entity(name = "menu_items")
data class MenuItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "store_id", nullable = false)
    val storeId: Long,

    @Column(nullable = false)
    val category: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val price: BigDecimal,

    @Column(nullable = true)
    val ingredients: String?,

    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = ConfigurationListConverter::class)
    @Column(columnDefinition = "jsonb", nullable = false)
    val configurations: List<Configuration>,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Clock.System.now(),

    @Column(name = "image_id", nullable = true)
    val imageId: Long?
) {
    data class Configuration(
        val name: String,
        val type: Type,
        val options: List<Option>
    ) {
        enum class Type { SINGLE_CHOICE, MULTIPLE_CHOICE, QUANTITY }

        data class Option(
            val name: String,
            val additionalPrice: BigDecimal
        )
    }
}