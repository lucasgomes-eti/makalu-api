package eti.lucasgomes.makalu.features.realtime.model

import eti.lucasgomes.makalu.shared.MakaluError

object RealtimeError {
    data object Unavailable : MakaluError("MK-801", "Realtime updates are unavailable")
}
