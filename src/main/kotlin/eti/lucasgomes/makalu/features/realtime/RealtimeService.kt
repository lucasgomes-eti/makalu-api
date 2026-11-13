package eti.lucasgomes.makalu.features.realtime

import eti.lucasgomes.makalu.features.auth.JwtService
import eti.lucasgomes.makalu.features.orders.OrderRepository
import eti.lucasgomes.makalu.features.orders.model.OrderError
import eti.lucasgomes.makalu.features.realtime.model.RealtimeCredentialsResponse
import eti.lucasgomes.makalu.features.realtime.model.RealtimeError
import eti.lucasgomes.makalu.features.stores.StoreRepository
import eti.lucasgomes.makalu.features.stores.model.StoreError
import eti.lucasgomes.makalu.shared.exceptions.NotFoundException
import eti.lucasgomes.makalu.shared.exceptions.ServiceUnavailableException
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class RealtimeService(
    private val orderRepository: OrderRepository,
    private val storeRepository: StoreRepository,
    private val realtimeGateway: RealtimeGateway,
    private val properties: RealtimeProperties,
    jwtService: JwtService
) {

    /** A key lives as long as an access token, so both are renewed on the same rhythm. */
    private val keyValidity = Duration.ofMillis(jwtService.accessTokenValidityMs)

    /** Someone else's order is reported exactly like a missing one. */
    fun credentialsForOrder(userId: Long, orderId: Long): RealtimeCredentialsResponse {
        orderRepository.findByIdAndUserId(orderId, userId)
            ?: throw NotFoundException(OrderError.OrderNotFound)
        return credentialsFor(RealtimeChannels.order(orderId))
    }

    /** Someone else's store is reported exactly like a missing one. */
    fun credentialsForStore(userId: Long, storeId: Long): RealtimeCredentialsResponse {
        val store = storeRepository.findById(storeId).orElse(null)
        if (store == null || store.ownerUserId != userId) {
            throw NotFoundException(StoreError.StoreNotFound)
        }
        return credentialsFor(RealtimeChannels.storeOrders(storeId))
    }

    private fun credentialsFor(channel: String): RealtimeCredentialsResponse {
        val key = try {
            realtimeGateway.issueReadKey(channel, keyValidity)
        } catch (exception: RealtimeUnavailableException) {
            throw ServiceUnavailableException(RealtimeError.Unavailable, exception)
        }
        return RealtimeCredentialsResponse(
            url = properties.emitter.publicUrl,
            channel = channel,
            key = key,
            expiresAt = Instant.now().plus(keyValidity).truncatedTo(ChronoUnit.SECONDS).toString()
        )
    }
}
