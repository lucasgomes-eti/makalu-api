package eti.lucasgomes.makalu.features.cart

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

@Component
class CartCleanupScheduler(
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val cartItemConfigurationRepository: CartItemConfigurationRepository,
) {

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    fun removeStaleCarts() {
        val cutoff = Clock.System.now().minus(48.hours)
        cartItemConfigurationRepository.deleteByCartUpdatedAtBefore(cutoff)
        cartItemRepository.deleteByCartUpdatedAtBefore(cutoff)
        cartRepository.deleteAllByUpdatedAtBefore(cutoff)
    }
}
