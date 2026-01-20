package eti.lucasgomes.makalu

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.concurrent.TimeUnit

@Component
class TokenCleanupScheduler(
    private val refreshTokenRepository: RefreshTokenRepository
) {

    // Runs every hour. Adjust 'fixedRate' as needed.
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    @Transactional
    fun removeExpiredTokens() {
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now())
    }
}