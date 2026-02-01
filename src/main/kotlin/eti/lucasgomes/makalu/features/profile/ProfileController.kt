package eti.lucasgomes.makalu.features.profile

import eti.lucasgomes.makalu.authenticatedUser
import eti.lucasgomes.makalu.shared.imageUpload.ImageCategory
import eti.lucasgomes.makalu.shared.imageUpload.ImageService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/profile")
class ProfileController(
    private val profileMapper: ProfileMapper,
    private val imageService: ImageService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @GetMapping
    fun getSelfProfile(): ProfileResponse {
        return profileMapper.toResponse(authenticatedUser)
    }

    @PostMapping("/upload-image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun uploadImage(
        @RequestParam("file")
        file: MultipartFile
    ) {
        val imageMetadataEntity = imageService.uploadImage(
            file = file,
            ownerUserId = authenticatedUser.id,
            relativeDirectory = "profile_images",
            category = ImageCategory.PROFILE
        )
        applicationEventPublisher.publishEvent(UploadProfileImageEvent(this, imageMetadataEntity))
    }

    @GetMapping("/image/{imageId}")
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