package eti.lucasgomes.makalu.features.realtime

import eti.lucasgomes.makalu.features.realtime.model.OrderRealtimeEvent
import java.time.Duration

/** The broker as the rest of the API sees it. Tests substitute a fake. */
interface RealtimeGateway {

    /** Whether the API currently holds a connection to the broker. */
    val isConnected: Boolean

    /**
     * Issues a read-only key for exactly [channel], valid for [ttl].
     *
     * @throws RealtimeUnavailableException when the broker cannot answer in time.
     */
    fun issueReadKey(channel: String, ttl: Duration): String

    /**
     * Publishes [event] to the order's channel and to its store's channel.
     *
     * Never blocks and never throws: a failure is logged and counted, nothing else.
     */
    fun publish(event: OrderRealtimeEvent)
}

class RealtimeUnavailableException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
