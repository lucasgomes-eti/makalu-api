package eti.lucasgomes.makalu.features.auth

import eti.lucasgomes.makalu.features.auth.model.RegisterRequest
import eti.lucasgomes.makalu.features.auth.model.RegisterResponse
import eti.lucasgomes.makalu.features.auth.model.UserEntity
import org.springframework.stereotype.Component

@Component
class RegisterMapper(
    private val hashEncoder: HashEncoder
) {
    fun toResponse(userEntity: UserEntity): RegisterResponse = userEntity.run {
        RegisterResponse(
            id = id,
            name = name,
            email = email,
            phoneNumber = phoneNumber
        )
    }

    fun toEntity(registerRequest: RegisterRequest): UserEntity = registerRequest.run {
        UserEntity(
            name = name,
            email = email,
            phoneNumber = phoneNumber,
            passwordHash = hashEncoder.encode(registerRequest.password)
        )
    }
}