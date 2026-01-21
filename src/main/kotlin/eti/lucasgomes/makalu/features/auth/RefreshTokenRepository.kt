package eti.lucasgomes.makalu.features.auth

import eti.lucasgomes.makalu.features.auth.model.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import java.time.Instant

interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, Long> {
    @Modifying
    fun deleteByExpiresAtBefore(now: Instant)

    fun findByUserIdAndHashedToken(userId: Long, hashedToken: String): RefreshTokenEntity?

    @Modifying
    fun deleteByUserIdAndHashedToken(userId: Long, hashedToken: String)

    @Modifying
    fun deleteByUserId(userId: Long)
}