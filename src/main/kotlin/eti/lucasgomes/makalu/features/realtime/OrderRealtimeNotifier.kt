package eti.lucasgomes.makalu.features.realtime

import eti.lucasgomes.makalu.features.realtime.model.OrderChangedEvent
import eti.lucasgomes.makalu.features.realtime.model.OrderRealtimeEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

/**
 * Tells the broker about an order only once its transaction has committed, so a client
 * that reacts by reading the order over REST always finds it. A rolled-back transaction
 * publishes nothing.
 */
@Component
class OrderRealtimeNotifier(private val realtimeGateway: RealtimeGateway) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun listen(event: OrderChangedEvent) {
        val order = event.order
        try {
            realtimeGateway.publish(
                OrderRealtimeEvent(
                    type = event.type,
                    orderId = order.id.toString(),
                    storeId = order.storeId.toString(),
                    status = order.status,
                    updatedAt = order.updatedAt.toString()
                )
            )
        } catch (exception: Exception) {
            // The order is already saved; realtime is best effort and must not fail the request.
            logger.warn("Realtime event {} for order {} was not published", event.type, order.id, exception)
        }
    }
}
