package eti.lucasgomes.makalu.features.cart

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class ConfigurationListConverter : AttributeConverter<List<CartItemEntity.Configuration>, String> {

    private val json = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: List<CartItemEntity.Configuration>?): String {
        return json.writeValueAsString(attribute ?: emptyList<CartItemEntity.Configuration>())
    }

    override fun convertToEntityAttribute(dbData: String?): List<CartItemEntity.Configuration> {
        return json.readValue(
            dbData ?: "[]",
            json.typeFactory.constructCollectionType(List::class.java, CartItemEntity.Configuration::class.java)
        )
    }

}