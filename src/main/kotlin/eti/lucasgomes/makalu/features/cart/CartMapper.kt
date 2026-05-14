package eti.lucasgomes.makalu.features.cart

import eti.lucasgomes.makalu.features.cart.model.CartItemRequest
import eti.lucasgomes.makalu.features.menu.model.MenuItemEntity
import org.springframework.stereotype.Component

@Component
class CartMapper {

    fun toEntity(
        request: CartItemRequest,
        cart: CartEntity,
        menuItem: MenuItemEntity
    ): CartItemEntity = CartItemEntity(
        id = 0,
        cart = cart,
        menuItem = menuItem,
        configurations = request.configurations!!.map {
            CartItemEntity.Configuration(
                name = it.name!!,
                quantity = it.quantity!!,
                price = it.price!!
            )
        }
    )
}
