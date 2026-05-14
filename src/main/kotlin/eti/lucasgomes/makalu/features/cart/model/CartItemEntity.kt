package eti.lucasgomes.makalu.features.cart.model

import eti.lucasgomes.makalu.features.menu.model.MenuItemEntity
import jakarta.persistence.*

@Entity(name = "cart_items")
data class CartItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "cart_id")
    val cart: CartEntity,

    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    val menuItem: MenuItemEntity,

    @Column(nullable = true, length = 240)
    val notes: String? = null,

    @OneToMany(mappedBy = "cartItem", cascade = [CascadeType.ALL], orphanRemoval = true)
    val configurations: MutableList<CartItemConfigurationEntity> = mutableListOf()
)