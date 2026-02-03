package eti.lucasgomes.makalu.shared.imageUpload

import jakarta.persistence.*
import kotlin.time.Clock
import kotlin.time.Instant

@Entity(name = "images")
data class ImageMetadataEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "original_name", nullable = false)
    val originalName: String,

    @Column(name = "stored_name", nullable = false)
    val storedName: String,

    @Column(name = "mime_type", nullable = false)
    val mimeType: String,

    @Column(name = "owner_user_id", nullable = false)
    val ownerUserId: Long,

    @Column(name = "size_in_bytes", nullable = false)
    val sizeInBytes: Long,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Clock.System.now(),

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    val category: ImageCategory
)
