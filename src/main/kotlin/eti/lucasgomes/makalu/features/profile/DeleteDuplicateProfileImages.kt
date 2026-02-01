package eti.lucasgomes.makalu.features.profile

import eti.lucasgomes.makalu.shared.imageUpload.ImageMetadataRepository
import eti.lucasgomes.makalu.shared.imageUpload.ImageService
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class DeleteDuplicateProfileImages(
    private val imageService: ImageService,
    private val imageMetadataRepository: ImageMetadataRepository
) {

    @Async
    @EventListener
    fun listen(uploadProfileImageEvent: UploadProfileImageEvent) {
        // find all images from profile category and owner user and delete all but the one from the event
        imageMetadataRepository.findByCategoryAndOwnerUserId(
            category = uploadProfileImageEvent.imageMetadataEntity.category,
            ownerUserId = uploadProfileImageEvent.imageMetadataEntity.ownerUserId,
        ).filter { it.id != uploadProfileImageEvent.imageMetadataEntity.id }
            .forEach {
                imageService.deleteImage(it.id)
            }
    }
}