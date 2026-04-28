package eti.lucasgomes.makalu.features.menu

import eti.lucasgomes.makalu.features.menu.model.MenuItemEntity
import eti.lucasgomes.makalu.features.menu.model.MenuItemRequest
import eti.lucasgomes.makalu.features.menu.model.MenuItemResponse
import org.springframework.stereotype.Component

@Component
class MenuMapper {

    fun toEntity(request: MenuItemRequest, storeId: Long, id: Long? = null): MenuItemEntity = request.run {
        MenuItemEntity(
            id = id ?: 0,
            storeId = storeId,
            category = category!!,
            name = name!!,
            price = price!!,
            ingredients = ingredients,
            configurations = configurations?.map {
                MenuItemEntity.Configuration(
                    name = it.name!!,
                    type = when (it.type!!) {
                        MenuItemRequest.Configuration.Type.SINGLE_CHOICE -> MenuItemEntity.Configuration.Type.SINGLE_CHOICE
                        MenuItemRequest.Configuration.Type.MULTIPLE_CHOICE -> MenuItemEntity.Configuration.Type.MULTIPLE_CHOICE
                        MenuItemRequest.Configuration.Type.QUANTITY -> MenuItemEntity.Configuration.Type.QUANTITY
                    },
                    options = it.options!!
                )
            } ?: emptyList(),
            imageId = null
        )
    }

    fun toResponse(entity: MenuItemEntity): MenuItemResponse = entity.run {
        MenuItemResponse(
            id = id,
            storeId = storeId,
            category = category,
            name = name,
            price = price,
            ingredients = ingredients,
            configurations = configurations.map {
                MenuItemResponse.Configuration(
                    name = it.name,
                    type = when (it.type) {
                        MenuItemEntity.Configuration.Type.SINGLE_CHOICE -> MenuItemResponse.Configuration.Type.SINGLE_CHOICE
                        MenuItemEntity.Configuration.Type.MULTIPLE_CHOICE -> MenuItemResponse.Configuration.Type.MULTIPLE_CHOICE
                        MenuItemEntity.Configuration.Type.QUANTITY -> MenuItemResponse.Configuration.Type.QUANTITY
                    },
                    options = it.options
                )
            },
            imageId = imageId
        )
    }
}