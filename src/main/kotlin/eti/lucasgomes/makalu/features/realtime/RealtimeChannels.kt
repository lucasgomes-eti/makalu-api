package eti.lucasgomes.makalu.features.realtime

/** Emitter channel paths. Every path ends with a slash; Emitter rejects it otherwise. */
object RealtimeChannels {
    /** Covers every channel the API publishes to. Only the API's own write key uses it. */
    const val ALL = "makalu/#/"

    fun storeOrders(storeId: Long) = "makalu/store/$storeId/orders/"

    fun order(orderId: Long) = "makalu/order/$orderId/"
}
