package eti.lucasgomes.makalu.support

import eti.lucasgomes.makalu.features.realtime.RealtimeGateway
import eti.lucasgomes.makalu.features.realtime.RealtimeUnavailableException
import eti.lucasgomes.makalu.features.realtime.model.OrderRealtimeEvent
import java.time.Duration
import java.util.concurrent.CopyOnWriteArrayList

class RecordingRealtimeGateway : RealtimeGateway {
    val published = CopyOnWriteArrayList<OrderRealtimeEvent>()
    val issuedKeys = CopyOnWriteArrayList<Pair<String, Duration>>()

    @Volatile
    var available = true

    @Volatile
    var failOnPublish = false

    override val isConnected: Boolean
        get() = available

    override fun issueReadOnlyKeyForExactChannel(channel: String, validity: Duration): String {
        if (!available) throw RealtimeUnavailableException("broker down")
        issuedKeys += channel to validity
        return "key-for-$channel"
    }

    override fun publishWithoutBlockingOrThrowing(event: OrderRealtimeEvent) {
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
