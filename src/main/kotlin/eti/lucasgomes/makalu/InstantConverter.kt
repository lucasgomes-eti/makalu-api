package eti.lucasgomes.makalu

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.sql.Timestamp
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@Converter(autoApply = true)
class InstantConverter : AttributeConverter<Instant, Timestamp> {
    override fun convertToDatabaseColumn(attribute: Instant?): Timestamp? {
        return attribute?.let { Timestamp.from(it.toJavaInstant()) }
    }

    override fun convertToEntityAttribute(dbData: Timestamp?): Instant? {
        return dbData?.toInstant()?.toKotlinInstant()
    }
}