package eti.lucasgomes.makalu.features.realtime

import eti.lucasgomes.makalu.features.realtime.model.OrderRealtimeEvent
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.Duration

/** Used while `realtime.enabled` is false: the API behaves exactly as it did before. */
@Component
@ConditionalOnProperty(prefix = "realtime", name = ["enabled"], havingValue = "false", matchIfMissing = true)
class DisabledRealtimeGateway : RealtimeGateway {

    override val isConnected: Boolean = false

    override fun issueReadKey(channel: String, ttl: Duration): String =
        throw RealtimeUnavailableException("Realtime is disabled")

    override fun publish(event: OrderRealtimeEvent) = Unit
}
