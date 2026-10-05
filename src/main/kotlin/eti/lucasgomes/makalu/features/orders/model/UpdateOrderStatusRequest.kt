package eti.lucasgomes.makalu.features.orders.model

import jakarta.validation.constraints.NotNull

data class UpdateOrderStatusRequest(
    @field:NotNull
    var status: OrderStatus?
)
