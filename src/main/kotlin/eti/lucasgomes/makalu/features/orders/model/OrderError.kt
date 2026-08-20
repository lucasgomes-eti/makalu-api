package eti.lucasgomes.makalu.features.orders.model

import eti.lucasgomes.makalu.shared.MakaluError

object OrderError {
    data object StoreNotFound : MakaluError("MK-701", "Store not found")
    data object CartNotFound : MakaluError("MK-702", "Cart not found")
    data object EmptyCart : MakaluError("MK-703", "Cart is empty")
    data object DeliveryAddressRequired : MakaluError("MK-704", "Delivery address required")
    data object OrderNotFound : MakaluError("MK-705", "Order not found")
}