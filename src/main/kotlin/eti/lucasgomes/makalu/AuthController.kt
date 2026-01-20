package eti.lucasgomes.makalu

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
        @RequestBody body: CreateAccountRequest
    ) {
        authService.register(body)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody body: AuthRequest
    ): TokenPair {
        return authService.login(body.email, body.password)
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody body: RefreshRequest
    ): TokenPair {
        return authService.refresh(body.refreshToken)
    }
}