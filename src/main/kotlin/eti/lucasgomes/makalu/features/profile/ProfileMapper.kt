package eti.lucasgomes.makalu.features.profile

import eti.lucasgomes.makalu.features.auth.model.UserEntity
import org.springframework.stereotype.Component

@Component
class ProfileMapper {
    fun toResponse(userEntity: UserEntity): ProfileResponse = userEntity.run {
        ProfileResponse(
            id = id,
            name = name,
            email = email,
            phoneNumber = phoneNumber,
            profileImageId = profileImageId
        )
    }
}