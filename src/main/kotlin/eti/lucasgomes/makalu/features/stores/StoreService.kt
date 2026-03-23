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

    fun findInArea(currentUserId: Long): List<StoreEntity> {
        val userAddress = addressService.findByUser(currentUserId)
        return storeRepository.findWithinDistance(userAddress.location.x, userAddress.location.y, 10000.0)
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
}