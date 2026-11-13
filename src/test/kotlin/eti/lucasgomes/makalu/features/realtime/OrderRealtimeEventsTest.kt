package eti.lucasgomes.makalu.features.realtime

import eti.lucasgomes.makalu.features.orders.OrderRepository
import eti.lucasgomes.makalu.features.orders.OrderService
import eti.lucasgomes.makalu.features.orders.model.OrderStatus
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import eti.lucasgomes.makalu.support.IntegrationTest
import eti.lucasgomes.makalu.support.RecordingRealtimeGateway
import eti.lucasgomes.makalu.support.TestData
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.transaction.support.TransactionTemplate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrderRealtimeEventsTest : IntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var data: TestData

    @Autowired
    private lateinit var gateway: RecordingRealtimeGateway

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    @BeforeEach
    fun reset() = gateway.reset()

    private data class PlacedOrder(val id: Long, val storeId: Long, val ownerToken: String)

    /** Places an order through the API and returns it. */
    private fun placeOrder(): PlacedOrder {
        val owner = data.user()
        val store = data.store(owner)
        val customer = data.user()
        val cart = data.cartFor(customer, store)

        val body = mockMvc.post("/orders") {
            header("Authorization", data.bearer(customer))
            contentType = MediaType.APPLICATION_JSON
            content = """{"store_id": ${store.id}, "cart_id": ${cart.id}}"""
        }.andExpect { status { isCreated() } }.andReturn().response.contentAsString

        val id = Regex("\"id\":(\\d+)").find(body)!!.groupValues[1].toLong()
        return PlacedOrder(id, store.id, data.bearer(owner))
    }

    private fun move(order: PlacedOrder, status: OrderStatus) =
        mockMvc.patch("/orders/${order.id}/status") {
            header("Authorization", order.ownerToken)
            contentType = MediaType.APPLICATION_JSON
            content = """{"status": "$status"}"""
        }

    @Test
    fun `creating an order publishes order_created`() {
        val order = placeOrder()

        val event = gateway.published.single()
        assertEquals(1, event.v)
        assertEquals("order.created", event.type)
        assertEquals(order.id.toString(), event.orderId)
        assertEquals(order.storeId.toString(), event.storeId)
        assertEquals(OrderStatus.PENDING, event.status)
        assertEquals(orderRepository.findById(order.id).get().updatedAt.toString().take(19), event.updatedAt.take(19))
    }

    @Test
    fun `every status transition publishes order_status_changed with the saved timestamp`() {
        val order = placeOrder()
        gateway.published.clear()

        listOf(OrderStatus.ACCEPTED, OrderStatus.IN_ROUTE, OrderStatus.FINISHED).forEach { status ->
            val body = move(order, status).andExpect { status { isOk() } }.andReturn().response.contentAsString

            val event = gateway.published.last()
            assertEquals("order.status_changed", event.type)
            assertEquals(order.id.toString(), event.orderId)
            assertEquals(order.storeId.toString(), event.storeId)
            assertEquals(status, event.status)
            // The event carries the same updated_at the REST response reports.
            assertTrue(body.contains("\"updated_at\":\"${event.updatedAt}\""), "updated_at differs: $body")
        }
        assertEquals(3, gateway.published.size)
        assertTrue(gateway.published.map { it.updatedAt }.zipWithNext().all { (a, b) -> a < b })
    }

    @Test
    fun `cancelling publishes order_status_changed`() {
        val order = placeOrder()
        gateway.published.clear()

        move(order, OrderStatus.CANCELLED).andExpect { status { isOk() } }

        assertEquals(OrderStatus.CANCELLED, gateway.published.single().status)
    }

    @Test
    fun `a refused transition publishes nothing`() {
        val order = placeOrder()
        gateway.published.clear()

        move(order, OrderStatus.FINISHED).andExpect { status { isBadRequest() } }

        assertTrue(gateway.published.isEmpty())
    }

    @Test
    fun `nothing is published when the transaction rolls back`() {
        val order = placeOrder()
        gateway.published.clear()

        transactionTemplate.execute { transaction ->
            orderService.updateOrderStatus(order.id, OrderStatus.ACCEPTED)
            transaction.setRollbackOnly()
        }

        assertEquals(OrderStatus.PENDING, orderRepository.findById(order.id).get().status)
        assertTrue(gateway.published.isEmpty())
    }

    @Test
    fun `a failing broker affects neither order creation nor a status change`() {
        gateway.failOnPublish = true

        val order = placeOrder()
        move(order, OrderStatus.ACCEPTED).andExpect {
            status { isOk() }
            jsonPath("$.status") { value("ACCEPTED") }
        }

        assertEquals(OrderStatus.ACCEPTED, orderRepository.findById(order.id).get().status)
        assertTrue(gateway.published.isEmpty())
    }

    @Test
    fun `the event on the wire has exactly the agreed fields and no personal data`() {
        placeOrder()

        val json = jacksonObjectMapper().readTree(jacksonObjectMapper().writeValueAsBytes(gateway.published.single()))

        assertEquals(
            setOf("v", "type", "orderId", "storeId", "status", "updatedAt"),
            json.fieldNames().asSequence().toSet()
        )
        assertTrue(json["orderId"].isTextual && json["storeId"].isTextual && json["v"].isInt)
    }
}
