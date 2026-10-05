package eti.lucasgomes.makalu.features.orders.model

enum class OrderStatus {
    PENDING,
    ACCEPTED,
    CANCELLED,
    IN_ROUTE,
    FINISHED;

    val nextStatuses: Set<OrderStatus>
        get() = when (this) {
            PENDING -> setOf(ACCEPTED, CANCELLED)
            ACCEPTED -> setOf(IN_ROUTE, CANCELLED)
            IN_ROUTE -> setOf(FINISHED, CANCELLED)
            CANCELLED, FINISHED -> emptySet()
        }

    fun canTransitionTo(next: OrderStatus): Boolean = next in nextStatuses
}
