package eti.lucasgomes.makalu.features.auth

import eti.lucasgomes.makalu.features.auth.model.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.core.userdetails.UserDetailsService

interface UserRepository : JpaRepository<UserEntity, Long>, UserDetailsService {
    fun findByEmail(email: String): UserEntity?

    override fun loadUserByUsername(username: String): MakaluUserDetails {
        return findByEmail(username)?.let { MakaluUserDetails(it) } ?: throw IllegalStateException("User not found.")
    }
}