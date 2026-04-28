package eti.lucasgomes.makalu.features.stores.model

import jakarta.persistence.*
import org.locationtech.jts.geom.Point
import java.math.BigDecimal
import kotlin.time.Clock
import kotlin.time.Instant

@Entity(name = "stores")
data class StoreEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @ManyToMany
    @JoinTable(
        name = "stores_categories",
        joinColumns = [JoinColumn(name = "store_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")]
    )
    val categories: MutableList<CategoryEntity>,

    @Column(name = "logo_image_id", nullable = true)
    val logoImageId: Long?,

    @Column(name = "cover_image_id", nullable = true)
    val coverImageId: Long?,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Clock.System.now(),

    @Column(name = "owner_user_id", nullable = false)
    val ownerUserId: Long,

    @Column(nullable = false)
    val location: Point,

    @Column(name = "delivery_fee", nullable = false)
    val deliveryFee: BigDecimal
)