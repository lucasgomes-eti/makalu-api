package eti.lucasgomes.makalu.features.realtime

import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient
import com.hivemq.client.mqtt.mqtt3.exceptions.Mqtt3SubAckException
import eti.lucasgomes.makalu.features.orders.model.OrderStatus
import eti.lucasgomes.makalu.features.realtime.model.OrderRealtimeEvent
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy
import org.testcontainers.containers.wait.strategy.Wait
import java.net.ServerSocket
import java.time.Duration
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Runs the real gateway against a real Emitter broker: key issue, subscribe, publish,
 * receive, and the things a read key must not be able to do.
 *
 * The license and secret key are generated for the run and thrown away with the container.
 */
class EmitterRealtimeGatewayIT {

    companion object {
        private const val IMAGE = "emitter/server:v3.1"
        private const val BROKER_PORT = 8080

        private val credentials: Pair<String, String> by lazy {
            GenericContainer(IMAGE)
                .withCommand("./emitter", "license", "new")
                .withStartupCheckStrategy(OneShotStartupCheckStrategy().withTimeout(Duration.ofSeconds(30)))
                .use { container ->
                    container.start()
                    val logs = container.logs
                    val license = Regex("new license: (\\S+)").find(logs)!!.groupValues[1]
                    val secretKey = Regex("new secret key: (\\S+)").find(logs)!!.groupValues[1]
                    license to secretKey
                }
        }

        private fun newBroker(): GenericContainer<*> = GenericContainer(IMAGE)
            .withEnv("EMITTER_LICENSE", credentials.first)
            .withExposedPorts(BROKER_PORT)
            .waitingFor(Wait.forHttp("/health").forPort(BROKER_PORT))

        private val broker: GenericContainer<*> by lazy { newBroker().apply { start() } }

        private val opened = mutableListOf<AutoCloseable>()

        @JvmStatic
        @AfterAll
        fun tearDown() {
            opened.forEach { runCatching { it.close() } }
        }
    }

    private val meters = SimpleMeterRegistry()

    private fun gateway(host: String, port: Int): EmitterRealtimeGateway {
        val properties = RealtimeProperties(
            enabled = true,
            timeout = Duration.ofSeconds(2),
            emitter = RealtimeProperties.Emitter(host = host, port = port, secretKey = credentials.second)
        )
        return EmitterRealtimeGateway(properties, meters).also { gateway ->
            gateway.start()
            opened += AutoCloseable { gateway.stop() }
        }
    }

    /** A client like the web and Android apps: MQTT 3.1.1 over WebSocket. */
    private fun webSocketClient(container: GenericContainer<*> = broker): Mqtt3BlockingClient =
        MqttClient.builder()
            .useMqttVersion3()
            .identifier("test-${UUID.randomUUID()}")
            .serverHost(container.host)
            .serverPort(container.getMappedPort(BROKER_PORT))
            .webSocketConfig().serverPath("").applyWebSocketConfig()
            .buildBlocking()
            .also { client ->
                client.toAsync().connect().get(10, TimeUnit.SECONDS)
                opened += AutoCloseable { client.disconnect() }
            }

    /**
     * Emitter delivers on `<channel>/` although the subscription was `<key>/<channel>/`, so a
     * per-subscription callback never matches: messages have to be read from the global stream.
     */
    private fun Mqtt3BlockingClient.collect(key: String, channel: String): MutableList<Pair<String, String>> {
        val received = CopyOnWriteArrayList<Pair<String, String>>()
        toAsync().publishes(MqttGlobalPublishFilter.ALL) {
            received += it.topic.toString() to String(it.payloadAsBytes)
        }
        toAsync().subscribeWith()
            .topicFilter("$key/$channel")
            .qos(MqttQos.AT_MOST_ONCE)
            .send()
            .get(10, TimeUnit.SECONDS)
        return received
    }

    private fun await(what: String, timeout: Duration = Duration.ofSeconds(15), condition: () -> Boolean) {
        val deadline = System.nanoTime() + timeout.toNanos()
        while (!condition()) {
            check(System.nanoTime() < deadline) { "Timed out waiting for: $what" }
            Thread.sleep(50)
        }
    }

    private fun event(orderId: Long, storeId: Long, status: OrderStatus = OrderStatus.ACCEPTED) = OrderRealtimeEvent(
        type = OrderRealtimeEvent.TYPE_STATUS_CHANGED,
        orderId = orderId.toString(),
        storeId = storeId.toString(),
        status = status,
        updatedAt = "2026-11-13T18:04:11Z"
    )

    private fun failures() = meters.counter(EmitterRealtimeGateway.PUBLISH_FAILURES_METRIC).count()

    @Test
    fun `an event reaches the order channel and the store channel of subscribers holding read keys`() {
        val gateway = gateway(broker.host, broker.getMappedPort(BROKER_PORT))
        await("connection") { gateway.isConnected }

        val orderKey = gateway.issueReadKey(RealtimeChannels.order(42), Duration.ofHours(24))
        val storeKey = gateway.issueReadKey(RealtimeChannels.storeOrders(7), Duration.ofHours(24))
        val customer = webSocketClient().collect(orderKey, RealtimeChannels.order(42))
        val store = webSocketClient().collect(storeKey, RealtimeChannels.storeOrders(7))

        // The write key is fetched just after connecting; publish until it is in place.
        await("delivery") {
            gateway.publish(event(orderId = 42, storeId = 7))
            customer.isNotEmpty() && store.isNotEmpty()
        }

        val expected = """{"v":1,"type":"order.status_changed","orderId":"42","storeId":"7",""" +
            """"status":"ACCEPTED","updatedAt":"2026-11-13T18:04:11Z"}"""
        assertEquals("makalu/order/42/" to expected, customer.first())
        assertEquals("makalu/store/7/orders/" to expected, store.first())
    }

    @Test
    fun `an event for another order or store is not delivered`() {
        val gateway = gateway(broker.host, broker.getMappedPort(BROKER_PORT))
        await("connection") { gateway.isConnected }
        val mine = webSocketClient().collect(
            gateway.issueReadKey(RealtimeChannels.order(100), Duration.ofHours(1)), RealtimeChannels.order(100)
        )
        val probe = webSocketClient().collect(
            gateway.issueReadKey(RealtimeChannels.order(101), Duration.ofHours(1)), RealtimeChannels.order(101)
        )

        await("delivery") {
            gateway.publish(event(orderId = 101, storeId = 8))
            probe.isNotEmpty()
        }

        assertTrue(mine.isEmpty())
    }

    @Test
    fun `a read key is scoped to exactly its channel`() {
        val gateway = gateway(broker.host, broker.getMappedPort(BROKER_PORT))
        await("connection") { gateway.isConnected }
        val key = gateway.issueReadKey(RealtimeChannels.order(200), Duration.ofHours(1))
        val client = webSocketClient()

        listOf("makalu/order/201/", "makalu/order/+/", "makalu/order/200/extra/", "makalu/store/7/orders/")
            .forEach { channel ->
                assertThrows<Mqtt3SubAckException>("subscribed to $channel") {
                    client.subscribeWith().topicFilter("$key/$channel").send()
                }
            }
    }

    @Test
    fun `a read key cannot publish`() {
        val gateway = gateway(broker.host, broker.getMappedPort(BROKER_PORT))
        await("connection") { gateway.isConnected }
        val key = gateway.issueReadKey(RealtimeChannels.order(300), Duration.ofHours(1))
        val listener = webSocketClient().collect(key, RealtimeChannels.order(300))
        val attacker = webSocketClient()

        attacker.publishWith().topic("$key/${RealtimeChannels.order(300)}").payload("forged".toByteArray()).send()
        // A legitimate event sent afterwards proves the channel works and the forged one was dropped.
        await("delivery") {
            gateway.publish(event(orderId = 300, storeId = 9))
            listener.isNotEmpty()
        }

        assertFalse(listener.any { it.second == "forged" })
    }

    @Test
    fun `without a broker nothing blocks or throws, and failures are counted`() {
        val closedPort = ServerSocket(0).use { it.localPort }
        val startedAt = System.nanoTime()

        val gateway = gateway("localhost", closedPort)
        gateway.publish(event(orderId = 1, storeId = 1))

        assertTrue(Duration.ofNanos(System.nanoTime() - startedAt) < Duration.ofSeconds(1), "start or publish blocked")
        assertFalse(gateway.isConnected)
        assertEquals(1.0, failures())
        assertThrows<RealtimeUnavailableException> { gateway.issueReadKey(RealtimeChannels.order(1), Duration.ofHours(1)) }
    }

    @Test
    fun `it connects on its own once a broker that was down at startup comes up`() {
        val port = ServerSocket(0).use { it.localPort }
        val gateway = gateway("localhost", port)
        assertFalse(gateway.isConnected)

        val lateBroker = newBroker().apply {
            portBindings = listOf("$port:$BROKER_PORT")
            start()
        }
        opened += AutoCloseable { lateBroker.stop() }

        await("reconnection", Duration.ofSeconds(60)) { gateway.isConnected }
        val key = gateway.issueReadKey(RealtimeChannels.order(400), Duration.ofHours(1))
        val listener = webSocketClient(lateBroker).collect(key, RealtimeChannels.order(400))
        await("delivery") {
            gateway.publish(event(orderId = 400, storeId = 4))
            listener.isNotEmpty()
        }
    }
}
