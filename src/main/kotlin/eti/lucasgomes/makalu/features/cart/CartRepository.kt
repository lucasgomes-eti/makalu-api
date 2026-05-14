package eti.lucasgomes.makalu.features.cart

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartRepository : JpaRepository<CartEntity, Long> {
    fun findByOwnerIdAndStoreId(ownerId: Long, storeId: Long): CartEntity?
}
