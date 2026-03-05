package eti.lucasgomes.makalu.features.address

import eti.lucasgomes.makalu.shared.exceptions.NotFoundException
import org.springframework.stereotype.Service

@Service
class AddressService(
    private val addressRepository: AddressRepository,
    private val addressMapper: AddressMapper
) {

    fun create(addressRequest: AddressRequest, ownerUserId: Long): AddressEntity {
        val entity = addressMapper.toEntity(addressRequest, ownerUserId)
        return addressRepository.save(entity)
    }

    fun findByUser(userId: Long): AddressEntity {
        return addressRepository.findByOwnerUserId(userId) ?: throw NotFoundException(AddressError.AddressNotFound)
    }
}