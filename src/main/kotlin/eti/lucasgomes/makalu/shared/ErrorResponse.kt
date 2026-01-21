package eti.lucasgomes.makalu.shared

import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorResponse(
    @JsonProperty("http_code")
    val httpCode: Int,
    val message: String,
    @JsonProperty("internal_code")
    val internalCode: String,
    @JsonProperty("field_errors")
    val fieldErrors: List<FieldError>
) {
    data class FieldError(val field: String, val message: String)
}