package eti.lucasgomes.makalu.features.auth

import eti.lucasgomes.makalu.features.auth.model.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val registerMapper: RegisterMapper
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @Valid @RequestBody body: RegisterRequest
    ): RegisterResponse {
        return registerMapper.toResponse(authService.register(body))
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody body: LoginRequest
    ): TokenPairResponse {
        return authService.login(body.email, body.password)
    }

    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody body: RefreshRequest
    ): TokenPairResponse {
        return authService.refresh(body.refreshToken)
    }
}