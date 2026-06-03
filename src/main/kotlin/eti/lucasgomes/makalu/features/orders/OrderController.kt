package eti.lucasgomes.makalu.features.orders

import eti.lucasgomes.makalu.authenticatedUser
import eti.lucasgomes.makalu.features.orders.model.CreateOrderRequest
import eti.lucasgomes.makalu.features.orders.model.OrderSimpleResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreateOrderRequest): OrderSimpleResponse =
        orderService.createOrder(userId = authenticatedUser.id, request = request)
}
