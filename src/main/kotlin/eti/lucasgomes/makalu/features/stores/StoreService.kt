package eti.lucasgomes.makalu.features.stores

import eti.lucasgomes.makalu.features.stores.model.StoreEntity
import eti.lucasgomes.makalu.features.stores.model.StoreError
import eti.lucasgomes.makalu.features.stores.model.StoreRequest
import eti.lucasgomes.makalu.shared.exceptions.NotFoundException
import org.springframework.stereotype.Service

@Service
class StoreService(
    private val storeRepository: StoreRepository,
    private val storeMapper: StoreMapper,
) {

    fun create(storeRequest: StoreRequest, ownerUserId: Long): StoreEntity {
        return storeRepository.save(storeMapper.toEntity(storeRequest, ownerUserId))
    }

    fun findAll(): List<StoreEntity> {
        return storeRepository.findAll()
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