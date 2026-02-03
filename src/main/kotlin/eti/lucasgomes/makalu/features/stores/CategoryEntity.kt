package eti.lucasgomes.makalu.features.stores

import jakarta.persistence.*

@Entity(name = "categories")
data class CategoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val description: String,
)
