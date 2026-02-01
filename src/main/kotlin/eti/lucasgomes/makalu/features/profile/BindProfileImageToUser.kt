package eti.lucasgomes.makalu.features.profile

import eti.lucasgomes.makalu.features.auth.UserRepository
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class BindProfileImageToUser(
    private val userRepository: UserRepository
) {

    @Async
    @EventListener
    fun listen(uploadProfileImageEvent: UploadProfileImageEvent) {
        userRepository.findById(uploadProfileImageEvent.imageMetadataEntity.ownerUserId)
            .ifPresent {
                userRepository.save(it.copy(profileImageId = uploadProfileImageEvent.imageMetadataEntity.id))
            }
    }
}