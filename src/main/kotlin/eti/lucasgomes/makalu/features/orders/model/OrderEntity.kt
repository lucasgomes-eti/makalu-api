package eti.lucasgomes.makalu.features.orders.model

import jakarta.persistence.*
import java.math.BigDecimal
import kotlin.time.Clock
import kotlin.time.Instant

@Entity(name = "orders")
data class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "store_id", nullable = false)
    val storeId: Long,

    @Column(name = "store_name", nullable = false)
    val storeName: String,

    @Column(name = "cart_id", nullable = false)
    val cartId: Long,

    @Column(name = "address_id", nullable = false)
    val deliveryAddressId: Long,

    @Column(name = "delivery_address_line", nullable = false)
    val deliveryAddressLine: String,

    @Column(name = "delivery_fee", nullable = false)
    val deliveryFee: BigDecimal,

    @Column(name = "total_price", nullable = false)
    val totalPrice: BigDecimal,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    val status: OrderStatus,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<OrderItemEntity> = mutableListOf(),

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Clock.System.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Clock.System.now(),
)
