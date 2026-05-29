package eti.lucasgomes.makalu.features.cart

import eti.lucasgomes.makalu.features.cart.model.CartItemConfigurationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import kotlin.time.Instant

@Repository
interface CartItemConfigurationRepository : JpaRepository<CartItemConfigurationEntity, Long> {
    @Transactional
    @Modifying
    @Query("delete from cart_item_configurations cic where cic.cartItem.cart.updatedAt < :cutoff")
    fun deleteByCartUpdatedAtBefore(@Param("cutoff") cutoff: Instant): Int
}
