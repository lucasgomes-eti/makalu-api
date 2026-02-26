package eti.lucasgomes.makalu.features.stores

import eti.lucasgomes.makalu.features.stores.model.UploadStoreCoverImageEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class BindStoreCoverImageToStore(private val storeRepository: StoreRepository) {

    @Async
    @EventListener
    fun listen(uploadStoreCoverImageEvent: UploadStoreCoverImageEvent) {
        storeRepository.findById(uploadStoreCoverImageEvent.storeId)
            .ifPresent {
                storeRepository.save(it.copy(coverImageId = uploadStoreCoverImageEvent.imageMetadataEntity.id))
            }
    }
}