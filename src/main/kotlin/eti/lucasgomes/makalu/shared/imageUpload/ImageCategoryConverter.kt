package eti.lucasgomes.makalu.shared.imageUpload

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class ImageCategoryConverter : AttributeConverter<ImageCategory, String> {
    override fun convertToDatabaseColumn(attribute: ImageCategory): String {
        return attribute.name
    }

    override fun convertToEntityAttribute(dbData: String): ImageCategory {
        return ImageCategory.entries.find { category -> category.name == dbData }!!
    }

}