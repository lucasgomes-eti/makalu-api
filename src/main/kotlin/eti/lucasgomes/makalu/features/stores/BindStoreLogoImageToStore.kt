package eti.lucasgomes.makalu.features.stores

import eti.lucasgomes.makalu.features.stores.model.UploadStoreLogoImageEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class BindStoreLogoImageToStore(private val storeRepository: StoreRepository) {

    @Async
    @EventListener
    fun listen(uploadStoreLogoImageEvent: UploadStoreLogoImageEvent) {
        storeRepository.findById(uploadStoreLogoImageEvent.storeId)
            .ifPresent {
                storeRepository.save(it.copy(logoImageId = uploadStoreLogoImageEvent.imageMetadataEntity.id))
            }
    }
}