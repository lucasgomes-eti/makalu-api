package eti.lucasgomes.makalu.features.cart

import eti.lucasgomes.makalu.features.address.AddressEntity
import eti.lucasgomes.makalu.features.cart.model.*
import eti.lucasgomes.makalu.features.menu.model.MenuItemConfigurationOptionEntity
import eti.lucasgomes.makalu.features.menu.model.MenuItemEntity
import eti.lucasgomes.makalu.features.stores.model.StoreEntity
import org.springframework.stereotype.Component
import java.math.BigDecimal

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
            menuItem = menuItem,
            notes = request.notes
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

    fun toResponse(
        cart: CartEntity,
        store: StoreEntity,
        address: AddressEntity?
    ): CartResponse {
        val items = cart.items.map { toItemResponse(it) }
        val itemsTotal = items.fold(BigDecimal.ZERO) { acc, item -> acc + item.price }
        return CartResponse(
            id = cart.id,
            deliveryAddressLine = address?.let { buildAddressLine(it) },
            items = items,
            total = CartResponse.Total(
                deliveryFee = store.deliveryFee,
                total = itemsTotal + store.deliveryFee
            )
        )
    }

    private fun buildAddressLine(address: AddressEntity): String =
        listOfNotNull(address.street, address.number).joinToString(", ")

    private fun toItemResponse(item: CartItemEntity): CartResponse.CartItemResponse {
        val configurationsExtra = item.configurations.fold(BigDecimal.ZERO) { acc, c ->
            acc + (c.option.additionalPrice * c.quantity.toBigDecimal())
        }
        val price = item.menuItem.price + configurationsExtra

        val configurations = item.configurations
            .groupBy { it.option.configuration }
            .map { (configuration, selections) ->
                CartResponse.CartItemResponse.Configuration(
                    configurationId = configuration.id,
                    name = configuration.name,
                    options = selections.map { selection ->
                        CartResponse.CartItemResponse.Configuration.Option(
                            id = selection.option.id,
                            name = selection.option.name,
                            additionalPrice = selection.option.additionalPrice,
                            quantity = selection.quantity
                        )
                    }
                )
            }

        return CartResponse.CartItemResponse(
            id = item.id,
            imageId = item.menuItem.imageId,
            name = item.menuItem.name,
            price = price,
            notes = item.notes,
            configurations = configurations
        )
    }
}
