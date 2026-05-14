package eti.lucasgomes.makalu.features.cart

import eti.lucasgomes.makalu.features.menu.model.MenuItemEntity
import eti.lucasgomes.makalu.features.stores.model.StoreEntity
import jakarta.persistence.*
import java.math.BigDecimal
import kotlin.time.Clock
import kotlin.time.Instant

@Entity(name = "carts")
data class CartEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(name = "owner_id", nullable = false)
    val ownerId: Long,

    @ManyToOne
    @JoinColumn(name = "store_id")
    val store: StoreEntity,

    @OneToMany(mappedBy = "cart")
    val items: List<CartItemEntity>,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Clock.System.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Clock.System.now()
)

@Entity(name = "cart_items")
data class CartItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne
    @JoinColumn(name = "cart_id")
    val cart: CartEntity,

    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    val menuItem: MenuItemEntity,

    @Convert(converter = ConfigurationListConverter::class)
    @Column(columnDefinition = "jsonb", nullable = false)
    val configurations: List<Configuration>
) {
    data class Configuration(
        val name: String,
        val quantity: Int,
        val price: BigDecimal
    )
}
