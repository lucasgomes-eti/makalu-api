package eti.lucasgomes.makalu.features.auth.model

import jakarta.persistence.*
import kotlin.time.Clock
import kotlin.time.Instant

@Entity(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 120)
    val name: String,

    @Column(nullable = false, unique = true, length = 120)
    val email: String,

    @Column(name = "phone_number", nullable = false, unique = true, length = 15)
    val phoneNumber: String,

    @Column(name = "password_hash", nullable = false)
    val passwordHash: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Clock.System.now(),

    @Column(name = "profile_image_id", nullable = true)
    val profileImageId: Long?
)