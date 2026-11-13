package eti.lucasgomes.makalu.features.realtime

import eti.lucasgomes.makalu.features.orders.OrderRepository
import eti.lucasgomes.makalu.features.orders.model.OrderEntity
import eti.lucasgomes.makalu.features.orders.model.OrderStatus
import eti.lucasgomes.makalu.support.IntegrationTest
import eti.lucasgomes.makalu.support.RecordingRealtimeGateway
import eti.lucasgomes.makalu.support.TestData
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RealtimeCredentialsTest : IntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var data: TestData

    @Autowired
    private lateinit var gateway: RecordingRealtimeGateway

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @BeforeEach
    fun reset() = gateway.reset()

    private fun orderOf(customerId: Long, storeId: Long): OrderEntity = orderRepository.save(
        OrderEntity(
            userId = customerId,
            storeId = storeId,
            storeName = "Store",
            orderNumber = "123456",
            cartId = 1,
            deliveryAddressId = 1,
            deliveryAddressLine = "Test Street, 1",
            deliveryFee = BigDecimal("5.00"),
            totalPrice = BigDecimal("25.00"),
            status = OrderStatus.PENDING
        )
    )

    @Test
    fun `a customer gets read credentials for their own order`() {
        val customer = data.user()
        val order = orderOf(customer.id, data.store(data.user()).id)
        val before = Instant.now()

        mockMvc.get("/orders/${order.id}/realtime") { header("Authorization", data.bearer(customer)) }
            .andExpect {
                status { isOk() }
                header { string("Cache-Control", "no-store") }
                jsonPath("$.url") { value("ws://localhost:8090/") }
                jsonPath("$.channel") { value("makalu/order/${order.id}/") }
                jsonPath("$.key") { value("key-for-makalu/order/${order.id}/") }
                jsonPath("$.expires_at") { exists() }
            }
            .andDo {
                handle { result ->
                    val expiresAt = Instant.parse(
                        Regex("\"expires_at\":\"([^\"]+)\"").find(result.response.contentAsString)!!.groupValues[1]
                    )
                    // 24 hours, the lifetime of an access token.
                    val lifetime = Duration.between(before, expiresAt)
                    assertTrue(lifetime > Duration.ofHours(23) && lifetime <= Duration.ofHours(24).plusMinutes(1))
                }
            }

        assertEquals(listOf("makalu/order/${order.id}/" to Duration.ofHours(24)), gateway.issuedKeys.toList())
    }

    @Test
    fun `a customer is refused credentials for someone else's order`() {
        val order = orderOf(data.user().id, data.store(data.user()).id)

        mockMvc.get("/orders/${order.id}/realtime") { header("Authorization", data.bearer(data.user())) }
            .andExpect {
                status { isNotFound() }
                jsonPath("$.internal_code") { value("MK-705") }
            }

        assertTrue(gateway.issuedKeys.isEmpty())
    }

    @Test
    fun `the store owner is refused credentials for a customer's order channel`() {
        val owner = data.user()
        val order = orderOf(data.user().id, data.store(owner).id)

        mockMvc.get("/orders/${order.id}/realtime") { header("Authorization", data.bearer(owner)) }
            .andExpect { status { isNotFound() } }

        assertTrue(gateway.issuedKeys.isEmpty())
    }

    @Test
    fun `a store owner gets read credentials for their store`() {
        val owner = data.user()
        val store = data.store(owner)

        mockMvc.get("/orders/stores/${store.id}/realtime") { header("Authorization", data.bearer(owner)) }
            .andExpect {
                status { isOk() }
                jsonPath("$.channel") { value("makalu/store/${store.id}/orders/") }
                jsonPath("$.key") { value("key-for-makalu/store/${store.id}/orders/") }
            }
    }

    @Test
    fun `a user is refused credentials for a store they do not own`() {
        val store = data.store(data.user())

        mockMvc.get("/orders/stores/${store.id}/realtime") { header("Authorization", data.bearer(data.user())) }
            .andExpect {
                status { isNotFound() }
                jsonPath("$.internal_code") { value("MK-401") }
            }

        assertTrue(gateway.issuedKeys.isEmpty())
    }

    @Test
    fun `a missing store is refused`() {
        mockMvc.get("/orders/stores/999999999/realtime") { header("Authorization", data.bearer(data.user())) }
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun `both endpoints require authentication`() {
        mockMvc.get("/orders/1/realtime").andExpect { status { isUnauthorized() } }
        mockMvc.get("/orders/stores/1/realtime").andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `credentials are unavailable, not an error, while the broker is down`() {
        val owner = data.user()
        val store = data.store(owner)
        gateway.available = false

        mockMvc.get("/orders/stores/${store.id}/realtime") { header("Authorization", data.bearer(owner)) }
            .andExpect {
                status { isServiceUnavailable() }
                jsonPath("$.internal_code") { value("MK-801") }
            }
    }
}
