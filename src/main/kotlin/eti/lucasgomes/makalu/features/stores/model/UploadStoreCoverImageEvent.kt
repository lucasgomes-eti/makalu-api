package eti.lucasgomes.makalu.features.stores.model

import eti.lucasgomes.makalu.shared.imageUpload.ImageMetadataEntity
import org.springframework.context.ApplicationEvent

class UploadStoreCoverImageEvent(source: Any, val imageMetadataEntity: ImageMetadataEntity, val storeId: Long) :
    ApplicationEvent(source)