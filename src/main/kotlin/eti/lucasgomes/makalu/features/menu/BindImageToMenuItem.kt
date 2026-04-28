package eti.lucasgomes.makalu.features.menu

import eti.lucasgomes.makalu.features.menu.model.UploadMenuImageEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class BindImageToMenuItem(private val menuItemsRepository: MenuItemsRepository) {

    @Async
    @EventListener
    fun listen(uploadMenuImageEvent: UploadMenuImageEvent) {
        menuItemsRepository.findById(uploadMenuImageEvent.menuItemId)
            .ifPresent {
                menuItemsRepository.save(it.copy(imageId = uploadMenuImageEvent.imageMetadataEntity.id))
            }
    }
}