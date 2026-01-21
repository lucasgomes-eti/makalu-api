package eti.lucasgomes.makalu.shared

object GlobalError {
    data class InternalError(val throwable: Throwable? = null) :
        MakaluError(
            "MK-001",
            "Internal server error." + if (throwable != null) " Cause: ${throwable.localizedMessage}" else ""
        )

    data object UserNotAuthenticated : MakaluError("MK-002", "User not authenticated.")
}