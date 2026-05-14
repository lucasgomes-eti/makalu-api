package eti.lucasgomes.makalu.features.menu.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity(name = "menu_item_configuration_options")
data class MenuItemConfigurationOptionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_configuration_id", nullable = false)
    val configuration: MenuItemConfigurationEntity,

    @Column(nullable = false)
    val name: String,

    @Column(name = "additional_price", nullable = false)
    val additionalPrice: BigDecimal
)
