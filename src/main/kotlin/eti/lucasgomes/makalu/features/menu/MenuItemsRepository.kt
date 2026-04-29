package eti.lucasgomes.makalu.features.menu

import eti.lucasgomes.makalu.features.menu.model.MenuItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MenuItemsRepository : JpaRepository<MenuItemEntity, Long> {
    fun findAllByStoreId(storeId: Long): List<MenuItemEntity>
}