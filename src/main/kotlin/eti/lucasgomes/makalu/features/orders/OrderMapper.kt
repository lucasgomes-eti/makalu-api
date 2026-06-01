package eti.lucasgomes.makalu.features.orders

import eti.lucasgomes.makalu.features.address.AddressEntity
import eti.lucasgomes.makalu.features.cart.model.CartEntity
import eti.lucasgomes.makalu.features.cart.model.CartItemEntity
import eti.lucasgomes.makalu.features.stores.model.StoreEntity
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class OrderMapper {

    fun toEntity(
        cart: CartEntity,
        store: StoreEntity,
        address: AddressEntity
    ): OrderEntity {
        val itemsTotal = cart.items.fold(BigDecimal.ZERO) { acc, item -> acc + itemPrice(item) }

        val order = OrderEntity(
            userId = cart.ownerId,
            storeId = store.id,
            cartId = cart.id,
            deliveryAddressId = address.id,
            deliveryAddressLine = buildAddressLine(address),
            deliveryFee = store.deliveryFee,
            totalPrice = itemsTotal + store.deliveryFee,
            status = OrderStatus.PENDING
        )

        cart.items.forEach { cartItem ->
            val orderItem = OrderItemEntity(
                order = order,
                menuItemId = cartItem.menuItem.id,
                category = cartItem.menuItem.category,
                name = cartItem.menuItem.name,
                price = cartItem.menuItem.price,
                notes = cartItem.notes,
                ingredients = cartItem.menuItem.ingredients,
                imageId = cartItem.menuItem.imageId
            )

            cartItem.configurations
                .groupBy { it.option.configuration }
                .forEach { (configuration, selections) ->
                    val orderConfiguration = OrderItemConfigurationEntity(
                        orderItem = orderItem,
                        name = configuration.name
                    )
                    selections.forEach { selection ->
                        orderConfiguration.options.add(
                            OrderItemConfigurationOptionEntity(
                                configuration = orderConfiguration,
                                name = selection.option.name,
                                additionalPrice = selection.option.additionalPrice,
                                quantity = selection.quantity
                            )
                        )
                    }
                    orderItem.configurations.add(orderConfiguration)
                }

            order.items.add(orderItem)
        }

        return order
    }

    fun toResponse(order: OrderEntity): OrderResponse =
        OrderResponse(
            id = order.id,
            status = order.status,
            deliveryAddressLine = order.deliveryAddressLine,
            items = order.items.map { toItemResponse(it) },
            total = OrderResponse.Total(
                deliveryFee = order.deliveryFee,
                total = order.totalPrice
            ),
            createdAt = order.createdAt.toString()
        )

    private fun itemPrice(item: CartItemEntity): BigDecimal {
        val configurationsExtra = item.configurations.fold(BigDecimal.ZERO) { acc, c ->
            acc + (c.option.additionalPrice * c.quantity.toBigDecimal())
        }
        return item.menuItem.price + configurationsExtra
    }

    private fun toItemResponse(item: OrderItemEntity): OrderResponse.OrderItemResponse {
        val configurationsExtra = item.configurations
            .flatMap { it.options }
            .fold(BigDecimal.ZERO) { acc, option ->
                acc + (option.additionalPrice * option.quantity.toBigDecimal())
            }

        return OrderResponse.OrderItemResponse(
            id = item.id,
            menuItemId = item.menuItemId,
            imageId = item.imageId,
            name = item.name,
            price = item.price + configurationsExtra,
            notes = item.notes,
            configurations = item.configurations.map { configuration ->
                OrderResponse.OrderItemResponse.Configuration(
                    id = configuration.id,
                    name = configuration.name,
                    options = configuration.options.map { option ->
                        OrderResponse.OrderItemResponse.Configuration.Option(
                            id = option.id,
                            name = option.name,
                            additionalPrice = option.additionalPrice,
                            quantity = option.quantity
                        )
                    }
                )
            }
        )
    }

    private fun buildAddressLine(address: AddressEntity): String =
        listOfNotNull(address.street, address.number).joinToString(", ")
}
