package eti.lucasgomes.makalu.features.address

import jakarta.persistence.*
import org.locationtech.jts.geom.Point
import kotlin.time.Clock
import kotlin.time.Instant

@Entity(name = "addresses")
data class AddressEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "zip_code", nullable = false)
    val zipCode: String,

    @Column(nullable = false)
    val street: String,

    @Column(nullable = true)
    val number: String?,

    @Column(nullable = true)
    val complement: String?,

    @Column(nullable = false)
    val location: Point,

    @Column(name = "owner_user_id", nullable = false)
    val ownerUserId: Long,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Clock.System.now()
)