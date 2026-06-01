package eti.lucasgomes.makalu.features.orders.model

import jakarta.persistence.*

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
