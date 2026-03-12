package eti.lucasgomes.makalu.features.stores

import eti.lucasgomes.makalu.features.stores.model.StoreEntity
import eti.lucasgomes.makalu.features.stores.model.StoreRequest
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
}