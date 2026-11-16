package eti.lucasgomes.makalu.features.realtime

import eti.lucasgomes.makalu.features.realtime.model.OrderRealtimeEvent
import java.time.Duration

interface RealtimeGateway {

    val isConnected: Boolean

    @Throws(RealtimeUnavailableException::class)
    fun issueReadOnlyKeyForExactChannel(channel: String, validity: Duration): String

    fun publishWithoutBlockingOrThrowing(event: OrderRealtimeEvent)
}

class RealtimeUnavailableException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
