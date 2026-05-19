package eti.lucasgomes.makalu.shared.imageUpload

import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/images")
class ImageController(private val imageService: ImageService) {

    @GetMapping("/{imageId}")
    fun getImage(@PathVariable imageId: Long): ResponseEntity<Resource> {
        val metadata = imageService.getImageMetadata(imageId)
        val resource = imageService.getImageResource(imageId)

        return ResponseEntity
            .ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                """attachment; filename="${metadata.originalName}""""
            )
            .contentType(MediaType.parseMediaType(metadata.mimeType))
            .contentLength(metadata.sizeInBytes)
            .body(resource)
    }
}