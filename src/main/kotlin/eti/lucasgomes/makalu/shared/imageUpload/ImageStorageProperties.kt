package eti.lucasgomes.makalu.shared.imageUpload

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "makalu.image-storage")
@Component
data class ImageStorageProperties(
    val basePath: String = "./images",
    val allowedMimeTypes: Set<String> = setOf("image/jpeg", "image/png")
)
