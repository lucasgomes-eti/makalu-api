package eti.lucasgomes.makalu

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.core.userdetails.UserDetailsService

interface UserRepository : JpaRepository<User, Long>, UserDetailsService {
    fun findByEmail(email: String): User?

    override fun loadUserByUsername(username: String): MakaluUserDetails {
        return findByEmail(username)?.let { MakaluUserDetails(it) } ?: throw IllegalStateException("User not found.")
    }
}