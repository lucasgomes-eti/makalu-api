package eti.lucasgomes.makalu.features.address

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository : JpaRepository<AddressEntity, Long> {
    @Query(
        value = "SELECT * FROM addresses WHERE ST_DWithin(location, ST_SetSRID(ST_MakePoint(?1, ?2), 4326), ?3)",
        nativeQuery = true
    )
    fun findWithinDistance(longitude: Double, latitude: Double, distanceInMeters: Double): List<AddressEntity>

    fun findByOwnerUserId(userId: Long): AddressEntity?
}