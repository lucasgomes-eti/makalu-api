package eti.lucasgomes.makalu.features.auth.model

import eti.lucasgomes.makalu.shared.MakaluError

object AuthError {
    data object Unauthorized : MakaluError("MK-101", "Unauthorized.")
    data object InvalidCredentials : MakaluError("MK-102", "Invalid credentials.")
    data object InvalidRefreshToken : MakaluError("MK-103", "Invalid refresh token.")
    data object RefreshTokenNotRecognized :
        MakaluError("MK-104", "Refresh token not recognized (maybe used or expired?).")

    data object InvalidToken : MakaluError("MK-105", "Invalid token.")
    data object UserNotFound : MakaluError("MK-106", "User not found.")
}