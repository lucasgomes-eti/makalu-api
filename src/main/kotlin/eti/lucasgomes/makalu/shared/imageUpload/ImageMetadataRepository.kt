package eti.lucasgomes.makalu.shared.imageUpload

import org.springframework.data.jpa.repository.JpaRepository

interface ImageMetadataRepository : JpaRepository<ImageMetadataEntity, Long> {
    fun findByCategoryAndOwnerUserId(category: ImageCategory, ownerUserId: Long): List<ImageMetadataEntity>
}