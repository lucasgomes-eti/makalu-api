package eti.lucasgomes.makalu.features.orders

import eti.lucasgomes.makalu.features.address.AddressRepository
import eti.lucasgomes.makalu.features.cart.CartRepository
import eti.lucasgomes.makalu.features.orders.model.CreateOrderRequest
import eti.lucasgomes.makalu.features.orders.model.OrderDetailedResponse
import eti.lucasgomes.makalu.features.orders.model.OrderError
import eti.lucasgomes.makalu.features.orders.model.OrderSimpleResponse
import eti.lucasgomes.makalu.features.stores.StoreRepository
import eti.lucasgomes.makalu.shared.exceptions.BadRequestException
import eti.lucasgomes.makalu.shared.exceptions.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
    private val storeRepository: StoreRepository,
    private val addressRepository: AddressRepository,
    private val orderMapper: OrderMapper
) {

    @Transactional
    fun createOrder(userId: Long, request: CreateOrderRequest): OrderSimpleResponse {
        val store = storeRepository.findById(request.storeId!!)
            .orElseThrow { NotFoundException(OrderError.StoreNotFound) }

        val cart = cartRepository.findByOwnerIdAndStoreId(userId, request.storeId)
            ?: throw NotFoundException(OrderError.CartNotFound)
        if (cart.id != request.cartId) {
            throw NotFoundException(OrderError.CartNotFound)
        }
        if (cart.items.isEmpty()) {
            throw BadRequestException(OrderError.EmptyCart)
        }

        val address = addressRepository.findByOwnerUserId(userId)
            ?: throw BadRequestException(OrderError.DeliveryAddressRequired)

        val order = orderRepository.save(orderMapper.toEntity(cart, store, address))
        cartRepository.delete(cart)
        return orderMapper.toSimpleResponse(order)
    }

    @Transactional(readOnly = true)
    fun getOrders(userId: Long): List<OrderSimpleResponse> =
        orderMapper.toSimpleResponse(orderRepository.findByUserIdOrderByCreatedAtDesc(userId))

    @Transactional(readOnly = true)
    fun getOrder(userId: Long, orderId: Long): OrderDetailedResponse {
        val order = orderRepository.findByIdAndUserId(orderId, userId)
            ?: throw NotFoundException(OrderError.OrderNotFound)
        return orderMapper.toDetailedResponse(order)
    }
}
