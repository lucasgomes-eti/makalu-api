package eti.lucasgomes.makalu.features.menu

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import eti.lucasgomes.makalu.features.menu.model.MenuItemEntity
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class ConfigurationListConverter : AttributeConverter<List<MenuItemEntity.Configuration>, String> {

    private val json = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: List<MenuItemEntity.Configuration>?): String {
        return json.writeValueAsString(attribute ?: emptyList<MenuItemEntity.Configuration>())
    }

    override fun convertToEntityAttribute(dbData: String?): List<MenuItemEntity.Configuration> {
        return json.readValue(
            dbData ?: "[]",
            json.typeFactory.constructCollectionType(List::class.java, MenuItemEntity.Configuration::class.java)
        )
    }

}