package eti.lucasgomes.makalu.features.cart

import eti.lucasgomes.makalu.features.menu.model.MenuItemConfigurationOptionEntity
import jakarta.persistence.*

@Entity(name = "cart_item_configurations")
data class CartItemConfigurationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_item_id", nullable = false)
    val cartItem: CartItemEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_configuration_option_id", nullable = false)
    val option: MenuItemConfigurationOptionEntity,

    @Column(nullable = false)
    val quantity: Int = 1
)
