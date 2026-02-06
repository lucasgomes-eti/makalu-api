package eti.lucasgomes.makalu.features.stores

import eti.lucasgomes.makalu.features.stores.model.StoreRequest
import eti.lucasgomes.makalu.features.stores.model.StoreResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/stores")
class StoreController(private val storeService: StoreService, private val storeMapper: StoreMapper) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: StoreRequest): StoreResponse {
        val store = storeService.create(request)
        return storeMapper.toResponse(store)
    }

    @GetMapping
    fun findAll(): List<StoreResponse> {
        return storeService.findAll().map { storeMapper.toResponse(it) }
    }
}