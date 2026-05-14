package eti.lucasgomes.makalu.features.cart

import eti.lucasgomes.makalu.features.cart.model.CartItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartItemRepository : JpaRepository<CartItemEntity, Long>
