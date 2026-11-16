package eti.lucasgomes.makalu.features.realtime.model

import eti.lucasgomes.makalu.features.orders.model.OrderEntity
import org.springframework.context.ApplicationEvent

class OrderChangedEvent(
    source: Any,
    val type: String,
    val order: OrderEntity
) : ApplicationEvent(source)
