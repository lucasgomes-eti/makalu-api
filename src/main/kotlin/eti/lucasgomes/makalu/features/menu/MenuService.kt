package eti.lucasgomes.makalu.features.menu

import eti.lucasgomes.makalu.features.menu.model.MenuItemEntity
import eti.lucasgomes.makalu.features.menu.model.MenuItemRequest
import org.springframework.stereotype.Service

@Service
class MenuService(
    private val repository: MenuItemsRepository,
    private val mapper: MenuMapper
) {

    fun create(request: MenuItemRequest, storeId: Long): MenuItemEntity {
        return repository.save(mapper.toEntity(request = request, storeId = storeId))
    }

    fun update(request: MenuItemRequest, storeId: Long, id: Long): MenuItemEntity {
        return repository.save(mapper.toEntity(request = request, storeId = storeId, id = id))
    }

    fun findByStoreId(storeId: Long): List<MenuItemEntity> {
        return repository.findAllByStoreId(storeId)
    }

    fun delete(id: Long) {
        repository.deleteById(id)
    }
}