package eti.lucasgomes.makalu

import jakarta.persistence.*
import java.time.Instant

@Entity(name = "refresh_tokens")
@Table(
    indexes = [Index(columnList = "expiresAt", name = "idx_refresh_tokens_expires_at")]
)
data class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: Instant,

    @Column(name = "hashed_token", nullable = false)
    val hashedToken: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)
