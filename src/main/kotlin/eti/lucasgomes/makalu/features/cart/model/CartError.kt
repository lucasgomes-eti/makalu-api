package eti.lucasgomes.makalu.features.cart.model

import eti.lucasgomes.makalu.shared.MakaluError

object CartError {
    data object StoreNotFound : MakaluError("MK-601", "Store not found")
    data object MenuItemNotFound : MakaluError("MK-602", "Menu item not found")
    data object ConfigurationOptionNotFound : MakaluError("MK-603", "Configuration option not found")
    data object CartItemNotFound : MakaluError("MK-604", "Cart item not found")
}
