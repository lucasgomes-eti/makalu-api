package eti.lucasgomes.makalu.features.profile

import eti.lucasgomes.makalu.shared.imageUpload.ImageMetadataEntity
import org.springframework.context.ApplicationEvent

class UploadProfileImageEvent(source: Any, val imageMetadataEntity: ImageMetadataEntity) : ApplicationEvent(source)