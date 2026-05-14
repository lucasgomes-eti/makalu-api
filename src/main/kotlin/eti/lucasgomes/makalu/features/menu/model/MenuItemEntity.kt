package eti.lucasgomes.makalu.features.menu.model

import jakarta.persistence.*
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

    @OneToMany(mappedBy = "menuItem", cascade = [CascadeType.ALL], orphanRemoval = true)
    val configurations: MutableList<MenuItemConfigurationEntity> = mutableListOf(),

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Clock.System.now(),

    @Column(name = "image_id", nullable = true)
    val imageId: Long?
)
