package eti.lucasgomes.makalu.features.orders.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity(name = "order_items")
data class OrderItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: OrderEntity,

    @Column(name = "menu_item_id", nullable = false)
    val menuItemId: Long,

    @Column(nullable = false)
    val category: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val price: BigDecimal,

    @Column(nullable = true, length = 240)
    val notes: String? = null,

    @Column(nullable = true)
    val ingredients: String?,

    @Column(name = "image_id", nullable = true)
    val imageId: Long?,

    @OneToMany(mappedBy = "orderItem", cascade = [CascadeType.ALL], orphanRemoval = true)
    val configurations: MutableList<OrderItemConfigurationEntity> = mutableListOf()
)
