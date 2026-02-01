package eti.lucasgomes.makalu.shared.imageUpload

import eti.lucasgomes.makalu.shared.MakaluError

object ImageError {
    data object EmptyFile : MakaluError("MK-201", "Empty file.")
    data object InvalidMimeType : MakaluError("MK-202", "Invalid mime type.")
    data class UploadError(val throwable: Throwable) :
        MakaluError("MK-203", "Error while writing file to disk. Cause: ${throwable.localizedMessage}")

    data object MissingFilename : MakaluError("MK-204", "Missing file name.")
    data object ImageNotFound : MakaluError("MK-205", "Image not found.")
    data class DeleteError(val throwable: Throwable) :
        MakaluError("MK-206", "Error while deleting file. Cause: ${throwable.localizedMessage}")
}