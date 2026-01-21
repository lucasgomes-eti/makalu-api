package eti.lucasgomes.makalu.shared

abstract class MakaluError(
    val code: String,
    val message: String,
    val fieldErrors: List<ErrorResponse.FieldError> = emptyList()
)