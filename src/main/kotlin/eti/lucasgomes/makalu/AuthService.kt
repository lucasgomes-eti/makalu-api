package eti.lucasgomes.makalu

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.util.*
import kotlin.time.ExperimentalTime

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    @OptIn(ExperimentalTime::class)
    fun register(createAccountRequest: CreateAccountRequest): User {
        return userRepository.save(
            User(
                name = createAccountRequest.name,
                email = createAccountRequest.email,
                phoneNumber = createAccountRequest.phoneNumber,
                passwordHash = hashEncoder.encode(createAccountRequest.password),
            )
        )
    }

    fun login(email: String, password: String): TokenPair {
        val user = userRepository.findByEmail(email) ?: throw BadCredentialsException("Invalid credentials.")

        if (hashEncoder.matches(password, user.passwordHash).not()) {
            throw BadCredentialsException("Invalid credentials.")
        }

        val newAccessToken = jwtService.generateAccessToken(user.id.toString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toString())

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPair(newAccessToken, newRefreshToken)
    }

    @Transactional
    fun refresh(refreshToken: String): TokenPair {
        if (jwtService.isRefreshTokenValid(refreshToken).not()) {
            throw IllegalArgumentException("Invalid refresh token.")
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Invalid refresh token.") }

        val hashed = hashToken(refreshToken)

        val stored = refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
            ?: throw IllegalArgumentException("Refresh token not recognized (maybe used or expired?).")

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)

        // Should never happen as the TokenCleanupScheduler removes expired tokens.
        if (stored.expiresAt.isBefore(Instant.now())) {
            throw IllegalArgumentException("Invalid refresh token.")
        }

        val newAccessToken = jwtService.generateAccessToken(userId.toString())
        val newRefreshToken = jwtService.generateRefreshToken(userId.toString())

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPair(newAccessToken, newRefreshToken)
    }

    private fun storeRefreshToken(userId: Long, rawRefreshToken: String) {
        val hashed = hashToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.deleteByUserId(userId)
        refreshTokenRepository.save(RefreshToken(userId = userId, hashedToken = hashed, expiresAt = expiresAt))
    }

    private fun hashToken(rawRefreshToken: String): String {
        val hashBytes = MessageDigest.getInstance("SHA-256").digest(rawRefreshToken.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}