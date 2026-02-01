package eti.lucasgomes.makalu.shared.imageUpload

import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.StreamUtils
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*

@Service
class LocalImageStorageService(
    private val properties: ImageStorageProperties,
    private val rootPath: Path = Paths.get(properties.basePath)
) {

    @Throws(IOException::class)
    fun storeFile(inputStream: InputStream, originalName: String, relativeDirectory: String): String {
        val finalDirectory = rootPath.resolve(relativeDirectory)
        Files.createDirectories(finalDirectory)

        val storedName = "${UUID.randomUUID()}${getFileExtension(originalName)?.let { ".$it" } ?: ""}"
        val filePath = finalDirectory.resolve(storedName)

        Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW).use { outputStream ->
            StreamUtils.copy(inputStream, outputStream)
        }

        return rootPath.relativize(filePath).toString()
    }

    private fun getFileExtension(fileName: String): String? {
        val lastDot = fileName.lastIndexOf('.')
        return if (lastDot == -1) null else fileName.substring(lastDot + 1)
    }

    @Throws(IOException::class)
    fun getFileResource(storedPath: String): Resource {
        val filePath = rootPath.resolve(storedPath).normalize().toAbsolutePath()
        val normalizedRoot = rootPath.normalize().toAbsolutePath()

        if (filePath.startsWith(normalizedRoot).not()) {
            throw IOException("Invalid file path: $filePath")
        }

        if (Files.notExists(filePath)) {
            throw IOException("File not found: $filePath")
        }

        return UrlResource(filePath.toUri())
    }

    fun deleteFile(storedName: String) {
        val filePath = rootPath.resolve(storedName)
        Files.deleteIfExists(filePath)
    }
}