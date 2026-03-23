package eti.lucasgomes.makalu.features.address

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository : JpaRepository<AddressEntity, Long> {
    @Query("SELECT * FROM addresses WHERE owner_user_id = ?1 LIMIT 1", nativeQuery = true)
    fun findByOwnerUserId(userId: Long): AddressEntity?
}