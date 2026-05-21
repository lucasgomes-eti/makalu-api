package eti.lucasgomes.makalu.features.cart

import eti.lucasgomes.makalu.features.cart.model.CartItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface CartItemRepository : JpaRepository<CartItemEntity, Long> {
    @Transactional
    fun deleteAllByCartId(cartId: Long)
}
