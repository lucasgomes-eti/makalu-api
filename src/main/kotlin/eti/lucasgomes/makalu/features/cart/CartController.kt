package eti.lucasgomes.makalu.features.cart

import eti.lucasgomes.makalu.authenticatedUser
import eti.lucasgomes.makalu.features.cart.model.CartItemRequest
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
}
