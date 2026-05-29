package eti.lucasgomes.makalu.features.cart

import eti.lucasgomes.makalu.features.cart.model.CartEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import kotlin.time.Instant

@Repository
interface CartRepository : JpaRepository<CartEntity, Long> {
    fun findByOwnerIdAndStoreId(ownerId: Long, storeId: Long): CartEntity?

    @Transactional
    fun deleteAllByUpdatedAtBefore(cutoff: Instant): Long
}
