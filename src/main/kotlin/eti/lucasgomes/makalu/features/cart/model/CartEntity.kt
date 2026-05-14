package eti.lucasgomes.makalu.features.cart.model

import eti.lucasgomes.makalu.features.stores.model.StoreEntity
import jakarta.persistence.*
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

