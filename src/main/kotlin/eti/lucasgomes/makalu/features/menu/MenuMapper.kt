package eti.lucasgomes.makalu.features.menu

import eti.lucasgomes.makalu.features.menu.model.*
import org.springframework.stereotype.Component

@Component
class MenuMapper {

    fun toEntity(request: MenuItemRequest, storeId: Long, id: Long? = null): MenuItemEntity {
        val menuItem = MenuItemEntity(
            id = id ?: 0,
            storeId = storeId,
            category = request.category!!,
            name = request.name!!,
            price = request.price!!,
            ingredients = request.ingredients,
            imageId = null
        )

        request.configurations?.forEach { configRequest ->
            val configuration = MenuItemConfigurationEntity(
                menuItem = menuItem,
                name = configRequest.name!!,
                type = when (configRequest.type!!) {
                    MenuItemRequest.Configuration.Type.SINGLE_CHOICE -> MenuItemConfigurationEntity.Type.SINGLE_CHOICE
                    MenuItemRequest.Configuration.Type.MULTIPLE_CHOICE -> MenuItemConfigurationEntity.Type.MULTIPLE_CHOICE
                    MenuItemRequest.Configuration.Type.QUANTITY -> MenuItemConfigurationEntity.Type.QUANTITY
                }
            )
            configRequest.options!!.forEach { optionRequest ->
                configuration.options.add(
                    MenuItemConfigurationOptionEntity(
                        configuration = configuration,
                        name = optionRequest.name!!,
                        additionalPrice = optionRequest.additionalPrice!!
                    )
                )
            }
            menuItem.configurations.add(configuration)
        }

        return menuItem
    }

    fun toResponse(entity: MenuItemEntity): MenuItemResponse = entity.run {
        MenuItemResponse(
            id = id,
            storeId = storeId,
            category = category,
            name = name,
            price = price,
            ingredients = ingredients,
            configurations = configurations.map { configuration ->
                MenuItemResponse.Configuration(
                    id = configuration.id,
                    name = configuration.name,
                    type = when (configuration.type) {
                        MenuItemConfigurationEntity.Type.SINGLE_CHOICE -> MenuItemResponse.Configuration.Type.SINGLE_CHOICE
                        MenuItemConfigurationEntity.Type.MULTIPLE_CHOICE -> MenuItemResponse.Configuration.Type.MULTIPLE_CHOICE
                        MenuItemConfigurationEntity.Type.QUANTITY -> MenuItemResponse.Configuration.Type.QUANTITY
                    },
                    options = configuration.options.map { option ->
                        MenuItemResponse.Configuration.Option(
                            id = option.id,
                            name = option.name,
                            additionalPrice = option.additionalPrice
                        )
                    }
                )
            },
            imageId = imageId
        )
    }
}
