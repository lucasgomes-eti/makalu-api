package eti.lucasgomes.makalu.features.stores.model

import jakarta.persistence.*

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
    val coverImageId: Long?
)