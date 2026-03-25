package eti.lucasgomes.makalu.features.menu.model

import eti.lucasgomes.makalu.shared.MakaluError

object MenuError {
    data object MenuNotFound : MakaluError("MK-501", "Menu not found")
}