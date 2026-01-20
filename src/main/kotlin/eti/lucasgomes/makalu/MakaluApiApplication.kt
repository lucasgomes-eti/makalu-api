package eti.lucasgomes.makalu

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

val user: User
    get() = (SecurityContextHolder.getContext().authentication?.principal as? MakaluUserDetails)?.user
        ?: throw IllegalStateException("User not authenticated.")
