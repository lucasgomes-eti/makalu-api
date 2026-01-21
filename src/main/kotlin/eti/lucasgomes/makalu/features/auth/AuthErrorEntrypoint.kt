package eti.lucasgomes.makalu.features.auth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import eti.lucasgomes.makalu.features.auth.model.AuthError
import eti.lucasgomes.makalu.shared.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class AuthErrorEntrypoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val errorResponse = ErrorResponse(
            httpCode = HttpStatus.UNAUTHORIZED.value(),
            message = AuthError.Unauthorized.message,
            internalCode = AuthError.Unauthorized.code,
            fieldErrors = AuthError.Unauthorized.fieldErrors
        )
        response.apply {
            contentType = "application/json;charset=UTF-8"
            characterEncoding = "UTF-8"
            status = HttpServletResponse.SC_UNAUTHORIZED
            writer.write(jacksonObjectMapper().writeValueAsString(errorResponse))
        }
    }
}