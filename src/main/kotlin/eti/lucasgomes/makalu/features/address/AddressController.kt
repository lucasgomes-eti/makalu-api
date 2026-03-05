package eti.lucasgomes.makalu.features.address

import eti.lucasgomes.makalu.authenticatedUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/addresses")
class AddressController(
    private val addressService: AddressService,
    private val addressMapper: AddressMapper
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: AddressRequest
    ): AddressResponse {
        return addressMapper.toResponse(addressService.create(request, authenticatedUser.id))
    }

    @GetMapping
    fun getSelfAddress(): AddressResponse {
        return addressMapper.toResponse(addressService.findByUser(authenticatedUser.id))
    }
}