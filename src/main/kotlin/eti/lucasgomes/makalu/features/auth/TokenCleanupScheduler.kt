package eti.lucasgomes.makalu.features.auth

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
class TokenCleanupScheduler(
    private val refreshTokenRepository: RefreshTokenRepository
) {

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    fun removeExpiredTokens() {
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now())
    }
}