package eti.lucasgomes.makalu.features.cart

import eti.lucasgomes.makalu.features.address.AddressRepository
import eti.lucasgomes.makalu.features.cart.model.CartEntity
import eti.lucasgomes.makalu.features.cart.model.CartError
import eti.lucasgomes.makalu.features.cart.model.CartItemRequest
import eti.lucasgomes.makalu.features.cart.model.CartResponse
import eti.lucasgomes.makalu.features.menu.MenuItemConfigurationOptionRepository
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
    private val menuItemConfigurationOptionRepository: MenuItemConfigurationOptionRepository,
    private val addressRepository: AddressRepository,
    private val cartMapper: CartMapper
) {

    @Transactional(readOnly = true)
    fun getCart(ownerId: Long, storeId: Long): CartResponse {
        val store = storeRepository.findById(storeId)
            .orElseThrow { NotFoundException(CartError.StoreNotFound) }
        val cart = cartRepository.findByOwnerIdAndStoreId(ownerId, storeId)
        val address = addressRepository.findByOwnerUserId(ownerId)
        return cartMapper.toResponse(cart, store, address)
    }

    @Transactional
    fun addItem(ownerId: Long, storeId: Long, menuItemId: Long, request: CartItemRequest) {
        val store = storeRepository.findById(storeId)
            .orElseThrow { NotFoundException(CartError.StoreNotFound) }
        val menuItem = menuItemsRepository.findById(menuItemId)
            .orElseThrow { NotFoundException(CartError.MenuItemNotFound) }

        val requestedOptionIds = request.configurations!!.map { it.menuItemConfigurationOptionId!! }
        val optionsById = if (requestedOptionIds.isEmpty()) {
            emptyMap()
        } else {
            val found = menuItemConfigurationOptionRepository
                .findAllByIdInAndConfigurationMenuItemId(requestedOptionIds, menuItemId)
            if (found.size != requestedOptionIds.toSet().size) {
                throw NotFoundException(CartError.ConfigurationOptionNotFound)
            }
            found.associateBy { it.id }
        }

        val cart = cartRepository.findByOwnerIdAndStoreId(ownerId, storeId)
            ?: cartRepository.save(
                CartEntity(
                    id = 0,
                    ownerId = ownerId,
                    store = store,
                    items = emptyList()
                )
            )
        cartItemRepository.save(cartMapper.toEntity(request, cart, menuItem, optionsById))
    }

    @Transactional
    fun removeItem(ownerId: Long, storeId: Long, cartItemId: Long): CartResponse {
        val item = cartItemRepository.findById(cartItemId)
            .orElseThrow { NotFoundException(CartError.CartItemNotFound) }
        if (item.cart.ownerId != ownerId || item.cart.store.id != storeId) {
            throw NotFoundException(CartError.CartItemNotFound)
        }
        cartItemRepository.delete(item)

        val store = storeRepository.findById(storeId)
            .orElseThrow { NotFoundException(CartError.StoreNotFound) }
        val cart = cartRepository.findByOwnerIdAndStoreId(ownerId, storeId)
        val address = addressRepository.findByOwnerUserId(ownerId)
        return cartMapper.toResponse(cart, store, address)
    }

    @Transactional
    fun clearCart(ownerId: Long, storeId: Long) {
        val cart = cartRepository.findByOwnerIdAndStoreId(ownerId, storeId) ?: return
        cartItemRepository.deleteAllByCartId(cart.id)
    }
}
