package eti.lucasgomes.makalu

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import java.time.Instant

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    @Modifying
    fun deleteByExpiresAtBefore(now: Instant)

    fun findByUserIdAndHashedToken(userId: Long, hashedToken: String): RefreshToken?

    @Modifying
    fun deleteByUserIdAndHashedToken(userId: Long, hashedToken: String)

    @Modifying
    fun deleteByUserId(userId: Long)
}