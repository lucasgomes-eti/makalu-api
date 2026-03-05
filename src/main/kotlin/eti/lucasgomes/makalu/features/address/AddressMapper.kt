package eti.lucasgomes.makalu.features.address

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.stereotype.Component

@Component
class AddressMapper {

    fun toEntity(request: AddressRequest, ownerUserId: Long): AddressEntity = request.run {
        val geometryFactory = GeometryFactory(PrecisionModel(), 4326)
        AddressEntity(
            zipCode = zipCode,
            street = street,
            number = number,
            complement = complement,
            location = geometryFactory.createPoint(
                Coordinate(longitude, latitude)
            ),
            ownerUserId = ownerUserId
        )
    }

    fun toResponse(entity: AddressEntity): AddressResponse = entity.run {
        AddressResponse(
            id = id,
            zipCode = zipCode,
            street = street,
            number = number,
            complement = complement,
            longitude = location.x,
            latitude = location.y
        )
    }
}