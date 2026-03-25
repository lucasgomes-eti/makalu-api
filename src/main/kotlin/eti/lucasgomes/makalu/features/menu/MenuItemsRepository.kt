package eti.lucasgomes.makalu.features.menu

import eti.lucasgomes.makalu.features.menu.model.MenuItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MenuItemsRepository : JpaRepository<MenuItemEntity, Long> {
    fun findAllByStoreId(storeId: Long): List<MenuItemEntity>

    @Query("SELECT * FROM menu_items WHERE store_id = ?1 AND id = ?2 LIMIT 1", nativeQuery = true)
    fun findByStoreIdAndId(storeId: Long, menuItemId: Long): MenuItemEntity?
}