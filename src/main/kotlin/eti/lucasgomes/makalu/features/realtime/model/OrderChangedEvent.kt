package eti.lucasgomes.makalu.features.realtime.model

import eti.lucasgomes.makalu.features.orders.model.OrderEntity
import org.springframework.context.ApplicationEvent

/** Raised inside the transaction that created an order or changed its status. */
class OrderChangedEvent(
    source: Any,
    val type: String,
    val order: OrderEntity
) : ApplicationEvent(source)
