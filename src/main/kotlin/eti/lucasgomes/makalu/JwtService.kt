package eti.lucasgomes.makalu

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@Service
class JwtService(
    @Value($$"${jwt.secret}") private val jwtSecret: String
) {

    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
    private val accessTokenValidityMs = 24.hours.inWholeMilliseconds
    val refreshTokenValidityMs = 15.days.inWholeMilliseconds

    private fun generateToken(
        userId: String,
        type: String,
        expiry: Long
    ): String {
        val now = Date()
        val expiryDate = Date(now.time + expiry)
        return Jwts.builder()
            .subject(userId)
            .claim(TOKEN_TYPE_KEY, type)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    fun generateAccessToken(userId: String): String {
        return generateToken(userId, ACCESS_TOKEN_TYPE, accessTokenValidityMs)
    }

    fun generateRefreshToken(userId: String): String {
        return generateToken(userId, REFRESH_TOKEN_TYPE, refreshTokenValidityMs)
    }

    fun isAccessTokenValid(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val tokenType = claims[TOKEN_TYPE_KEY] as? String ?: return false
        return tokenType == ACCESS_TOKEN_TYPE
    }

    fun isRefreshTokenValid(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val tokenType = claims[TOKEN_TYPE_KEY] as? String ?: return false
        return tokenType == REFRESH_TOKEN_TYPE
    }

    fun getUserIdFromToken(token: String): Long {
        val claims = parseAllClaims(token) ?: throw IllegalArgumentException("Invalid token.")
        return claims.subject.toLongOrNull() ?: throw IllegalArgumentException("Invalid token.")
    }

    private fun parseAllClaims(token: String): Claims? {
        return try {
            val rawToken = if (token.startsWith("Bearer ")) {
                token.removePrefix("Bearer ")
            } else token
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        private const val TOKEN_TYPE_KEY = "type"
        private const val ACCESS_TOKEN_TYPE = "access"
        private const val REFRESH_TOKEN_TYPE = "refresh"
    }
}