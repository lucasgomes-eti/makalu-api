package eti.lucasgomes.makalu.features.address

import eti.lucasgomes.makalu.shared.MakaluError

object AddressError {
    data object AddressNotFound : MakaluError("MK-301", "Address not found")
}