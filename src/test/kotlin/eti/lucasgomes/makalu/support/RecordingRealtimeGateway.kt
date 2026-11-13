package eti.lucasgomes.makalu.support

import eti.lucasgomes.makalu.features.realtime.RealtimeGateway
import eti.lucasgomes.makalu.features.realtime.RealtimeUnavailableException
import eti.lucasgomes.makalu.features.realtime.model.OrderRealtimeEvent
import java.time.Duration
import java.util.concurrent.CopyOnWriteArrayList

/** Stands in for the broker: records what would have been sent. */
class RecordingRealtimeGateway : RealtimeGateway {

    val published = CopyOnWriteArrayList<OrderRealtimeEvent>()
    val issuedKeys = CopyOnWriteArrayList<Pair<String, Duration>>()

    @Volatile
    var available = true

    @Volatile
    var failOnPublish = false

    override val isConnected: Boolean
        get() = available

    override fun issueReadKey(channel: String, ttl: Duration): String {
        if (!available) throw RealtimeUnavailableException("broker down")
        issuedKeys += channel to ttl
        return "key-for-$channel"
    }

    override fun publish(event: OrderRealtimeEvent) {
        if (failOnPublish) throw IllegalStateException("broker down")
        published += event
    }

    fun reset() {
        published.clear()
        issuedKeys.clear()
        available = true
        failOnPublish = false
    }
}
