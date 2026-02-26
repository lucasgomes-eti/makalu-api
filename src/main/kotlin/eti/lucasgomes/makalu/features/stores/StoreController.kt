package eti.lucasgomes.makalu.features.stores

import eti.lucasgomes.makalu.authenticatedUser
import eti.lucasgomes.makalu.features.stores.model.StoreRequest
import eti.lucasgomes.makalu.features.stores.model.StoreResponse
import eti.lucasgomes.makalu.features.stores.model.UploadStoreLogoImageEvent
import eti.lucasgomes.makalu.shared.imageUpload.ImageCategory
import eti.lucasgomes.makalu.shared.imageUpload.ImageService
import eti.lucasgomes.makalu.shared.imageUpload.ImageUploadResponse
import jakarta.validation.Valid
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/stores")
class StoreController(
    private val storeService: StoreService,
    private val storeMapper: StoreMapper,
    private val imageService: ImageService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: StoreRequest): StoreResponse {
        val store = storeService.create(request, authenticatedUser.id)
        return storeMapper.toResponse(store)
    }

    @GetMapping
    fun findAll(): List<StoreResponse> {
        return storeService.findAll().map { storeMapper.toResponse(it) }
    }

    @PostMapping("/{storeId}/upload-logo-image")
    fun uploadLogoImage(
        @PathVariable("storeId")
        storeId: Long,
        @RequestParam("file")
        file: MultipartFile
    ): ImageUploadResponse {
        val imageMetadataEntity = imageService.uploadImage(
            file = file,
            ownerUserId = authenticatedUser.id,
            relativeDirectory = "store_images",
            category = ImageCategory.STORE_LOGO
        )
        applicationEventPublisher.publishEvent(UploadStoreLogoImageEvent(this, imageMetadataEntity, storeId))
        return ImageUploadResponse(imageMetadataEntity.id)
    }

    @GetMapping("/logo-image/{imageId}")
    fun getLogoImage(@PathVariable imageId: Long): ResponseEntity<Resource> {
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

    @PostMapping("/{storeId}/upload-cover-image")
    fun uploadCoverImage(
        @PathVariable("storeId")
        storeId: Long,
        @RequestParam("file")
        file: MultipartFile
    ): ImageUploadResponse {
        val imageMetadataEntity = imageService.uploadImage(
            file = file,
            ownerUserId = authenticatedUser.id,
            relativeDirectory = "store_images",
            category = ImageCategory.STORE_COVER
        )
        applicationEventPublisher.publishEvent(UploadStoreLogoImageEvent(this, imageMetadataEntity, storeId))
        return ImageUploadResponse(imageMetadataEntity.id)
    }

    @GetMapping("/cover-image/{imageId}")
    fun getCoverImage(@PathVariable imageId: Long): ResponseEntity<Resource> {
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