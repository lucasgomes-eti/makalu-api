package eti.lucasgomes.makalu.features.stores

import eti.lucasgomes.makalu.features.address.AddressService
import eti.lucasgomes.makalu.features.stores.model.StoreEntity
import eti.lucasgomes.makalu.features.stores.model.StoreError
import eti.lucasgomes.makalu.features.stores.model.StoreRequest
import eti.lucasgomes.makalu.shared.exceptions.NotFoundException
import org.springframework.stereotype.Service

@Service
class StoreService(
    private val storeRepository: StoreRepository,
    private val storeMapper: StoreMapper,
    private val addressService: AddressService
) {

    fun create(storeRequest: StoreRequest, ownerUserId: Long): StoreEntity {
        return storeRepository.save(storeMapper.toEntity(storeRequest, ownerUserId))
    }

    fun findInArea(currentUserId: Long, categories: List<Long>? = null): List<StoreEntity> {
        val userAddress = addressService.findByUser(currentUserId)
        if (categories != null) {
            return storeRepository.findWithinDistanceByCategories(
                longitude = userAddress.location.x,
                latitude = userAddress.location.y,
                distanceInMeters = STORE_SEARCH_AREA_IN_METERS,
                categories = categories
            )
        }
        return storeRepository.findWithinDistance(
            longitude = userAddress.location.x,
            latitude = userAddress.location.y,
            distanceInMeters = STORE_SEARCH_AREA_IN_METERS
        )
    }

    fun findByOwner(ownerUserId: Long): List<StoreEntity> {
        return storeRepository.findByOwnerUserId(ownerUserId)
    }

    fun update(storeRequest: StoreRequest, ownerUserId: Long, storeId: Long) {
        storeRepository.save(storeMapper.toEntity(storeRequest, ownerUserId, storeId))
    }

    fun delete(storeId: Long) {
        storeRepository.deleteById(storeId)
    }

    fun findById(storeId: Long): StoreEntity {
        return storeRepository.findById(storeId).orElseThrow { NotFoundException(StoreError.StoreNotFound) }
    }

    companion object {
        private const val STORE_SEARCH_AREA_IN_METERS = 10_000.0
    }
}