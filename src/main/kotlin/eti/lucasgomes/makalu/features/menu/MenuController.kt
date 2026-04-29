package eti.lucasgomes.makalu.features.menu

import eti.lucasgomes.makalu.authenticatedUser
import eti.lucasgomes.makalu.features.menu.model.MenuItemRequest
import eti.lucasgomes.makalu.features.menu.model.MenuItemResponse
import eti.lucasgomes.makalu.features.menu.model.UploadMenuImageEvent
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
class MenuController(
    private val menuService: MenuService,
    private val menuMapper: MenuMapper,
    private val imageService: ImageService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @PostMapping("/{storeId}/menu")
    @ResponseStatus(HttpStatus.CREATED)
    fun post(
        @Valid @RequestBody request: MenuItemRequest,
        @PathVariable storeId: Long
    ): MenuItemResponse {
        return menuMapper.toResponse(menuService.create(request = request, storeId = storeId))
    }

    @PutMapping("/{storeId}/menu/{menuItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(
        @Valid @RequestBody request: MenuItemRequest,
        @PathVariable storeId: Long,
        @PathVariable menuItemId: Long
    ): MenuItemResponse {
        return menuMapper.toResponse(menuService.update(request = request, storeId = storeId, id = menuItemId))
    }

    @GetMapping("/{storeId}/menu")
    fun get(
        @PathVariable storeId: Long
    ): List<MenuItemResponse> {
        return menuService.findByStoreId(storeId).map { menuMapper.toResponse(it) }
    }

    @GetMapping("/menu/{menuItemId}")
    fun getById(
        @PathVariable menuItemId: Long
    ): MenuItemResponse {
        return menuMapper.toResponse(menuService.findById(menuItemId = menuItemId))
    }

    @DeleteMapping("/{storeId}/menu/{menuItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable menuItemId: Long
    ) {
        menuService.delete(menuItemId)
    }

    @PostMapping("/{storeId}/menu/{menuItemId}/upload-image")
    fun uploadImage(
        @PathVariable storeId: Long,
        @PathVariable menuItemId: Long,
        @RequestParam file: MultipartFile
    ): ImageUploadResponse {
        val imageMetadataEntity = imageService.uploadImage(
            file = file,
            ownerUserId = authenticatedUser.id,
            relativeDirectory = "menu_images",
            category = ImageCategory.MENU_ITEM
        )
        applicationEventPublisher.publishEvent(UploadMenuImageEvent(this, imageMetadataEntity, storeId, menuItemId))
        return ImageUploadResponse(imageMetadataEntity.id)
    }

    @GetMapping("/{storeId}/menu/{menuItemId}/image/{imageId}")
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