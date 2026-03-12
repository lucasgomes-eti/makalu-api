package eti.lucasgomes.makalu.features.stores

import eti.lucasgomes.makalu.features.stores.model.CategoryResponse
import eti.lucasgomes.makalu.features.stores.model.StoreEntity
import eti.lucasgomes.makalu.features.stores.model.StoreRequest
import eti.lucasgomes.makalu.features.stores.model.StoreResponse
import org.springframework.stereotype.Component

@Component
class StoreMapper(private val categoryService: CategoryService) {

    fun toEntity(request: StoreRequest, ownerUserId: Long, storeId: Long = 0): StoreEntity = request.run {
        StoreEntity(
            id = storeId,
            name = name,
            categories = categoryService.findAllByIds(categoriesIds).toMutableList(),
            logoImageId = null,
            coverImageId = null,
            ownerUserId = ownerUserId
        )
    }

    fun toResponse(entity: StoreEntity): StoreResponse = entity.run {
        StoreResponse(
            id = id,
            name = name,
            categories = categories.map { CategoryResponse(it.id, it.description) },
            logoImageId = logoImageId,
            coverImageId = coverImageId
        )
    }
}