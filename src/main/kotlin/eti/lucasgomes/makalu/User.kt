package eti.lucasgomes.makalu

import jakarta.persistence.*
import kotlin.time.Clock
import kotlin.time.Instant

@Entity(name = "users")
data class User(
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
    val createdAt: Instant = Clock.System.now()
)