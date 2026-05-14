package eti.lucasgomes.makalu.features.cart

import eti.lucasgomes.makalu.features.cart.model.CartItemRequest
import eti.lucasgomes.makalu.features.menu.model.MenuItemConfigurationOptionEntity
import eti.lucasgomes.makalu.features.menu.model.MenuItemEntity
import org.springframework.stereotype.Component

@Component
class CartMapper {

    fun toEntity(
        request: CartItemRequest,
        cart: CartEntity,
        menuItem: MenuItemEntity,
        optionsById: Map<Long, MenuItemConfigurationOptionEntity>
    ): CartItemEntity {
        val cartItem = CartItemEntity(
            cart = cart,
            menuItem = menuItem
        )
        request.configurations!!.forEach { configRequest ->
            cartItem.configurations.add(
                CartItemConfigurationEntity(
                    cartItem = cartItem,
                    option = optionsById.getValue(configRequest.menuItemConfigurationOptionId!!),
                    quantity = configRequest.quantity ?: 1
                )
            )
        }
        return cartItem
    }
}
