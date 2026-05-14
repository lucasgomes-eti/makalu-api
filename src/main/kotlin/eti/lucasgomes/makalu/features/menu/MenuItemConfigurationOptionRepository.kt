package eti.lucasgomes.makalu.features.menu

import eti.lucasgomes.makalu.features.menu.model.MenuItemConfigurationOptionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MenuItemConfigurationOptionRepository : JpaRepository<MenuItemConfigurationOptionEntity, Long> {
    fun findAllByIdInAndConfigurationMenuItemId(
        ids: Collection<Long>,
        menuItemId: Long
    ): List<MenuItemConfigurationOptionEntity>
}
