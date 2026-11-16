package eti.lucasgomes.makalu.features.realtime

object RealtimeChannels {
    const val EVERY_MAKALU_CHANNEL = "makalu/#/"

    fun storeOrders(storeId: Long) = "makalu/store/$storeId/orders/"

    fun order(orderId: Long) = "makalu/order/$orderId/"
}
