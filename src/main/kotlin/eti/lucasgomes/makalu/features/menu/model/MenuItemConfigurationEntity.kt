package eti.lucasgomes.makalu.features.menu.model

import jakarta.persistence.*

@Entity(name = "menu_item_configurations")
data class MenuItemConfigurationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    val menuItem: MenuItemEntity,

    @Column(nullable = false)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: Type,

    @OneToMany(mappedBy = "configuration", cascade = [CascadeType.ALL], orphanRemoval = true)
    val options: MutableList<MenuItemConfigurationOptionEntity> = mutableListOf()
) {
    enum class Type { SINGLE_CHOICE, MULTIPLE_CHOICE, QUANTITY }
}
