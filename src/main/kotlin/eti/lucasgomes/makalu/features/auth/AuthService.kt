package eti.lucasgomes.makalu.features.auth

import eti.lucasgomes.makalu.features.auth.model.*
import eti.lucasgomes.makalu.shared.exceptions.AuthErrorException
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
    fun register(registerRequest: RegisterRequest): UserEntity {
        return userRepository.save(
            UserEntity(
                name = registerRequest.name,
                email = registerRequest.email,
                phoneNumber = registerRequest.phoneNumber,
                passwordHash = hashEncoder.encode(registerRequest.password),
            )
        )
    }

    @Transactional
    fun login(email: String, password: String): TokenPairResponse {
        val user = userRepository.findByEmail(email) ?: throw AuthErrorException(AuthError.InvalidCredentials)

        if (hashEncoder.matches(password, user.passwordHash).not()) {
            throw AuthErrorException(AuthError.InvalidCredentials)
        }

        val newAccessToken = jwtService.generateAccessToken(user.id.toString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toString())

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPairResponse(newAccessToken, newRefreshToken)
    }

    @Transactional
    fun refresh(refreshToken: String): TokenPairResponse {
        if (jwtService.isRefreshTokenValid(refreshToken).not()) {
            throw AuthErrorException(AuthError.InvalidRefreshToken)
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(userId).orElseThrow { AuthErrorException(AuthError.InvalidRefreshToken) }

        val hashed = hashToken(refreshToken)

        val stored = refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
            ?: throw AuthErrorException(AuthError.RefreshTokenNotRecognized)

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)

        // Should never happen as the TokenCleanupScheduler removes expired tokens.
        if (stored.expiresAt.isBefore(Instant.now())) {
            throw AuthErrorException(AuthError.InvalidRefreshToken)
        }

        val newAccessToken = jwtService.generateAccessToken(userId.toString())
        val newRefreshToken = jwtService.generateRefreshToken(userId.toString())

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPairResponse(newAccessToken, newRefreshToken)
    }

    private fun storeRefreshToken(userId: Long, rawRefreshToken: String) {
        val hashed = hashToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.deleteByUserId(userId)
        refreshTokenRepository.save(RefreshTokenEntity(userId = userId, hashedToken = hashed, expiresAt = expiresAt))
    }

    private fun hashToken(rawRefreshToken: String): String {
        val hashBytes = MessageDigest.getInstance("SHA-256").digest(rawRefreshToken.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}