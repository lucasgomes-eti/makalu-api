package eti.lucasgomes.makalu.features.stores

import eti.lucasgomes.makalu.features.stores.model.StoreEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface StoreRepository : JpaRepository<StoreEntity, Long> {
    @Query(
        value = "SELECT * FROM stores WHERE ST_DWithin(location::geography, ST_SetSRID(ST_MakePoint(?1, ?2), 4326)::geography, ?3)",
        nativeQuery = true
    )
    fun findWithinDistance(longitude: Double, latitude: Double, distanceInMeters: Double): List<StoreEntity>

    fun findByOwnerUserId(ownerUserId: Long): List<StoreEntity>
}