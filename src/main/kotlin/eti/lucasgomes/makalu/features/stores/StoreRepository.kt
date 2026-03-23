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

    @Query(
        value = "SELECT DISTINCT s.* FROM stores s JOIN stores_categories sc ON s.id = sc.store_id WHERE ST_DWithin(s.location::geography, ST_SetSRID(ST_MakePoint(?1, ?2), 4326)::geography, ?3) AND sc.category_id IN (?4)",
        nativeQuery = true
    )
    fun findWithinDistanceByCategories(
        longitude: Double,
        latitude: Double,
        distanceInMeters: Double,
        categories: List<Long>
    ): List<StoreEntity>

    fun findByOwnerUserId(ownerUserId: Long): List<StoreEntity>
}