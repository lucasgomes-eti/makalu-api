package eti.lucasgomes.makalu.features.stores.model

import jakarta.persistence.*
import kotlin.time.Clock
import kotlin.time.Instant

@Entity(name = "categories")
data class CategoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val description: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Clock.System.now()
)