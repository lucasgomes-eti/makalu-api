package eti.lucasgomes.makalu.shared.imageUpload

import eti.lucasgomes.makalu.shared.exceptions.BadRequestException
import eti.lucasgomes.makalu.shared.exceptions.InternalErrorException
import eti.lucasgomes.makalu.shared.exceptions.NotFoundException
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Service
class ImageService(
    private val properties: ImageStorageProperties,
    private val repository: ImageMetadataRepository,
    private val storageService: LocalImageStorageService
) {
    fun uploadImage(
        file: MultipartFile,
        ownerUserId: Long,
        relativeDirectory: String,
        category: ImageCategory
    ): ImageMetadataEntity {
        return withValidImage(file) {
            val storagePath = try {
                inputStream.use { inputStream ->
                    storageService.storeFile(inputStream, originalFilename!!, relativeDirectory)
                }
            } catch (e: IOException) {
                throw InternalErrorException(ImageError.UploadError(e))
            }

            val metadata = ImageMetadataEntity(
                originalName = originalFilename!!,
                storedName = storagePath,
                mimeType = contentType!!,
                ownerUserId = ownerUserId,
                sizeInBytes = size,
                category = category
            )

            repository.save(metadata)
        }
    }

    private fun <T> withValidImage(file: MultipartFile, block: MultipartFile.() -> T): T {
        if (file.originalFilename.isNullOrBlank()) {
            throw BadRequestException(ImageError.MissingFilename)
        }

        if (file.isEmpty) {
            throw BadRequestException(ImageError.EmptyFile)
        }

        val mimeType = file.contentType

        if (mimeType == null || mimeType !in properties.allowedMimeTypes) {
            throw BadRequestException(ImageError.InvalidMimeType)
        }

        return block(file)
    }

    fun getImageResource(imageId: Long): Resource {
        val metadata = getImageMetadata(imageId)
        return storageService.getFileResource(metadata.storedName)
    }

    fun getImageMetadata(imageId: Long): ImageMetadataEntity =
        repository.findById(imageId).orElseThrow { NotFoundException(ImageError.ImageNotFound) }

    fun deleteImage(imageId: Long) {
        val metadata = getImageMetadata(imageId)
        try {
            storageService.deleteFile(metadata.storedName)
            repository.delete(metadata)
        } catch (e: Exception) {
            throw InternalErrorException(ImageError.DeleteError(e))
        }
    }

}