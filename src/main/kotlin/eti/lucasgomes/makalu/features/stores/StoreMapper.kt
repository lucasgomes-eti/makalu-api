package eti.lucasgomes.makalu.features.stores

import eti.lucasgomes.makalu.features.stores.model.CategoryResponse
import eti.lucasgomes.makalu.features.stores.model.StoreEntity
import eti.lucasgomes.makalu.features.stores.model.StoreRequest
import eti.lucasgomes.makalu.features.stores.model.StoreResponse
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.stereotype.Component

@Component
class StoreMapper(private val categoryService: CategoryService) {

    fun toEntity(request: StoreRequest, ownerUserId: Long, storeId: Long = 0): StoreEntity = request.run {
        val geometryFactory = GeometryFactory(PrecisionModel(), 4326)
        StoreEntity(
            id = storeId,
            name = name,
            categories = categoryService.findAllByIds(categoriesIds).toMutableList(),
            logoImageId = null,
            coverImageId = null,
            ownerUserId = ownerUserId,
            location = geometryFactory.createPoint(
                Coordinate(longitude!!, latitude!!)
            )
        )
    }

    fun toResponse(entity: StoreEntity): StoreResponse = entity.run {
        StoreResponse(
            id = id,
            name = name,
            categories = categories.map { CategoryResponse(it.id, it.description) },
            logoImageId = logoImageId,
            coverImageId = coverImageId,
            longitude = location.x,
            latitude = location.y
        )
    }
}