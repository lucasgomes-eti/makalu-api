package eti.lucasgomes.makalu.features.orders

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

enum class OrderStatus {
    PENDING,
    ACCEPTED,
    CANCELLED,
    IN_ROUTE,
    FINISHED,
}

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

@Entity(name = "order_item_configurations")
data class OrderItemConfigurationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    val orderItem: OrderItemEntity,

    @Column(nullable = false)
    val name: String,

    @OneToMany(mappedBy = "configuration", cascade = [CascadeType.ALL], orphanRemoval = true)
    val options: MutableList<OrderItemConfigurationOptionEntity> = mutableListOf()
)

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
