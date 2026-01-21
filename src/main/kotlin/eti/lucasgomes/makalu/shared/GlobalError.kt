package eti.lucasgomes.makalu.shared

object GlobalError {

    data class InternalError(val throwable: Throwable? = null) :
        MakaluError(
            "MK-001",
            "Internal server error." + if (throwable != null) " Cause: ${throwable.localizedMessage}" else ""
        )

    data object UserNotAuthenticated : MakaluError("MK-002", "User not authenticated.")

    data object InvalidRequestObject : MakaluError("MK-003", "Request object has validation errors.")

    const val EMAIL_TAKEN_MESSAGE: String = "Email is unavailable."
    const val PHONE_NUMBER_TAKEN_MESSAGE: String = "Phone number is unavailable."
}