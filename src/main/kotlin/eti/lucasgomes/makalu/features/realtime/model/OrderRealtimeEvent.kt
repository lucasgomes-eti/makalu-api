package eti.lucasgomes.makalu.features.realtime.model

import eti.lucasgomes.makalu.features.orders.model.OrderStatus

/**
 * The message sent over MQTT. It is a hint that something changed, never the source of
 * truth, and carries no personal data: clients fetch the details over REST.
 */
data class OrderRealtimeEvent(
    val v: Int = VERSION,
    val type: String,
    val orderId: String,
    val storeId: String,
    val status: OrderStatus,
    val updatedAt: String
) {
    companion object {
        const val VERSION = 1
        const val TYPE_CREATED = "order.created"
        const val TYPE_STATUS_CHANGED = "order.status_changed"
    }
}
