package eti.lucasgomes.makalu.features.orders.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity(name = "order_item_configuration_options")
data class OrderItemConfigurationOptionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_configuration_id", nullable = false)
    val configuration: OrderItemConfigurationEntity,

    @Column(nullable = false)
    val name: String,

    @Column(name = "additional_price", nullable = false)
    val additionalPrice: BigDecimal,

    @Column(nullable = false)
    val quantity: Int = 1
)
