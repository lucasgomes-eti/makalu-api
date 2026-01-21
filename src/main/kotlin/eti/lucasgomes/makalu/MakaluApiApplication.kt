package eti.lucasgomes.makalu

import eti.lucasgomes.makalu.features.auth.MakaluUserDetails
import eti.lucasgomes.makalu.features.auth.model.UserEntity
import eti.lucasgomes.makalu.shared.GlobalError
import eti.lucasgomes.makalu.shared.exceptions.InternalErrorException
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.core.context.SecurityContextHolder

@SpringBootApplication
@EnableScheduling
class MakaluApiApplication

fun main(args: Array<String>) {
    runApplication<MakaluApiApplication>(*args)
}

val userEntity: UserEntity
    get() = (SecurityContextHolder.getContext().authentication?.principal as? MakaluUserDetails)?.userEntity
        ?: throw InternalErrorException(GlobalError.UserNotAuthenticated)
