package eti.lucasgomes.makalu.features.auth

import eti.lucasgomes.makalu.features.auth.model.LoginRequest
import eti.lucasgomes.makalu.features.auth.model.RefreshRequest
import eti.lucasgomes.makalu.features.auth.model.RegisterRequest
import eti.lucasgomes.makalu.features.auth.model.TokenPairResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody body: RegisterRequest
    ) {
        authService.register(body)
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