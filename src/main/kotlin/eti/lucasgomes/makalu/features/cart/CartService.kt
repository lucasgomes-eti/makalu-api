package eti.lucasgomes.makalu.features.cart

import eti.lucasgomes.makalu.features.cart.model.CartError
import eti.lucasgomes.makalu.features.cart.model.CartItemRequest
import eti.lucasgomes.makalu.features.menu.MenuItemsRepository
import eti.lucasgomes.makalu.features.stores.StoreRepository
import eti.lucasgomes.makalu.shared.exceptions.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val storeRepository: StoreRepository,
    private val menuItemsRepository: MenuItemsRepository,
    private val cartMapper: CartMapper
) {

    @Transactional
    fun addItem(ownerId: Long, storeId: Long, menuItemId: Long, request: CartItemRequest) {
        val store = storeRepository.findById(storeId)
            .orElseThrow { NotFoundException(CartError.StoreNotFound) }
        val menuItem = menuItemsRepository.findById(menuItemId)
            .orElseThrow { NotFoundException(CartError.MenuItemNotFound) }
        val cart = cartRepository.findByOwnerIdAndStoreId(ownerId, storeId)
            ?: cartRepository.save(
                CartEntity(
                    id = 0,
                    ownerId = ownerId,
                    store = store,
                    items = emptyList()
                )
            )
        cartItemRepository.save(cartMapper.toEntity(request, cart, menuItem))
    }
}
