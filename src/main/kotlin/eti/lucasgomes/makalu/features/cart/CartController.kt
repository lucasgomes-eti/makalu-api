package eti.lucasgomes.makalu.features.cart

import eti.lucasgomes.makalu.authenticatedUser
import eti.lucasgomes.makalu.features.cart.model.CartItemRequest
import eti.lucasgomes.makalu.features.cart.model.CartResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/stores")
class CartController(private val cartService: CartService) {

    @PutMapping("/{storeId}/menu/{menuItemId}/cart")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun addItem(
        @PathVariable storeId: Long,
        @PathVariable menuItemId: Long,
        @Valid @RequestBody request: CartItemRequest
    ) {
        cartService.addItem(
            ownerId = authenticatedUser.id,
            storeId = storeId,
            menuItemId = menuItemId,
            request = request
        )
    }

    @GetMapping("/{storeId}/cart")
    fun get(@PathVariable storeId: Long): CartResponse =
        cartService.getCart(ownerId = authenticatedUser.id, storeId = storeId)

    @DeleteMapping("/{storeId}/cart/items/{cartItemId}")
    fun removeItem(
        @PathVariable storeId: Long,
        @PathVariable cartItemId: Long
    ): CartResponse = cartService.removeItem(
        ownerId = authenticatedUser.id,
        storeId = storeId,
        cartItemId = cartItemId
    )

    @DeleteMapping("/{storeId}/cart/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun clearCart(@PathVariable storeId: Long) {
        cartService.clearCart(ownerId = authenticatedUser.id, storeId = storeId)
    }
}
