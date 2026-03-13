package eti.lucasgomes.makalu.features.stores.model

import eti.lucasgomes.makalu.shared.MakaluError

object StoreError {
    data object StoreNotFound : MakaluError("MK-401", "Store not found")
}