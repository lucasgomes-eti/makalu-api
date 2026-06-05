package eti.lucasgomes.makalu.features.orders

import eti.lucasgomes.makalu.features.orders.model.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<OrderEntity, Long> {
    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<OrderEntity>
}
