package eti.lucasgomes.makalu.features.menu.model

import eti.lucasgomes.makalu.shared.imageUpload.ImageMetadataEntity
import org.springframework.context.ApplicationEvent

class UploadMenuImageEvent(
    source: Any,
    val imageMetadataEntity: ImageMetadataEntity,
    val storeId: Long,
    val menuItemId: Long
) :
    ApplicationEvent(source)