package eti.lucasgomes.makalu.features.realtime

import eti.lucasgomes.makalu.features.realtime.model.OrderChangedEvent
import eti.lucasgomes.makalu.features.realtime.model.OrderRealtimeEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OrderRealtimeNotifier(private val realtimeGateway: RealtimeGateway) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun publishOnceTheOrderIsCommitted(event: OrderChangedEvent) {
        val order = event.order
        try {
            realtimeGateway.publishWithoutBlockingOrThrowing(
                OrderRealtimeEvent(
                    type = event.type,
                    orderId = order.id.toString(),
                    storeId = order.storeId.toString(),
                    status = order.status,
                    updatedAt = order.updatedAt.toString()
                )
            )
        } catch (exception: Exception) {
            logger.warn("Realtime event {} for order {} was not published", event.type, order.id, exception)
        }
    }
}
