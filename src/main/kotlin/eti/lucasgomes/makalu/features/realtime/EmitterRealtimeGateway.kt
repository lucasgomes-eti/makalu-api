package eti.lucasgomes.makalu.features.realtime

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.lifecycle.MqttDisconnectSource
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import eti.lucasgomes.makalu.features.realtime.model.OrderRealtimeEvent
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

/**
 * Talks to a self-hosted Emitter broker over MQTT 3.1.1 (plain TCP, private network).
 *
 * Emitter specifics this class relies on, all checked against a real broker:
 * - a topic is `<key>/<channel>/`;
 * - a key is requested by publishing to `emitter/keygen/`, and the answer arrives on the
 *   same connection, on that same topic, without a subscription;
 * - a refused publish is not reported by MQTT itself, only by a message on `emitter/error/`.
 */
@Component
@ConditionalOnProperty(prefix = "realtime", name = ["enabled"], havingValue = "true")
class EmitterRealtimeGateway(
    private val properties: RealtimeProperties,
    meterRegistry: MeterRegistry
) : RealtimeGateway {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val mapper = jacksonObjectMapper()
    private val timeoutMs = properties.timeout.toMillis()

    private val publishFailures: Counter = Counter.builder(PUBLISH_FAILURES_METRIC)
        .description("Order events that could not be handed to the broker")
        .register(meterRegistry)

    /** Runs key requests off the MQTT client's own threads, which must never block. */
    private val worker = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "realtime-keygen").apply { isDaemon = true }
    }

    /** Emitter answers key requests in order and an error carries no channel: one at a time. */
    private val keygenLock = ReentrantLock()

    @Volatile
    private var pendingKeygen: CompletableFuture<KeygenResponse>? = null

    /** The API's own key for publishing. Requested again after every (re)connect. */
    @Volatile
    private var writeKey: String? = null

    private val client: Mqtt3AsyncClient = MqttClient.builder()
        .useMqttVersion3()
        .identifier("makalu-api-${UUID.randomUUID()}")
        .serverHost(properties.emitter.host)
        .serverPort(properties.emitter.port)
        .automaticReconnect()
        .initialDelay(RECONNECT_INITIAL_DELAY_SECONDS, TimeUnit.SECONDS)
        .maxDelay(RECONNECT_MAX_DELAY_SECONDS, TimeUnit.SECONDS)
        .applyAutomaticReconnect()
        .addConnectedListener {
            logger.info("Connected to the realtime broker at {}:{}", properties.emitter.host, properties.emitter.port)
            worker.execute(::refreshWriteKey)
        }
        .addDisconnectedListener { context ->
            writeKey = null
            if (context.source == MqttDisconnectSource.USER) {
                logger.info("Disconnected from the realtime broker")
            } else {
                logger.warn("Disconnected from the realtime broker ({}): {}", context.source, context.cause.message)
            }
        }
        .buildAsync()

    init {
        Gauge.builder(CONNECTED_METRIC) { if (isConnected) 1.0 else 0.0 }
            .description("1 while the API is connected to the realtime broker")
            .register(meterRegistry)
    }

    override val isConnected: Boolean
        get() = client.state.isConnected

    /** Starts connecting and returns at once; the client keeps retrying on its own. */
    @PostConstruct
    fun start() {
        client.publishes(MqttGlobalPublishFilter.ALL, ::onBrokerMessage)
        client.connectWith()
            .cleanSession(true)
            .keepAlive(KEEP_ALIVE_SECONDS)
            .send()
            .whenComplete { _, error ->
                if (error != null) {
                    logger.warn("Could not reach the realtime broker yet, retrying in the background: {}", error.message)
                }
            }
    }

    @PreDestroy
    fun stop() {
        worker.shutdownNow()
        if (client.state.isConnectedOrReconnect) {
            client.disconnect()
        }
    }

    override fun issueReadKey(channel: String, ttl: Duration): String =
        requestKey(channel, type = "r", ttlSeconds = ttl.seconds)

    override fun publish(event: OrderRealtimeEvent) {
        try {
            val key = writeKey
            if (!isConnected || key == null) {
                onPublishFailure(event, "not connected to the broker")
                return
            }
            val payload = mapper.writeValueAsBytes(event)
            val channels = listOf(
                RealtimeChannels.order(event.orderId.toLong()),
                RealtimeChannels.storeOrders(event.storeId.toLong())
            )
            channels.forEach { channel ->
                client.publishWith()
                    .topic("$key/$channel")
                    .qos(MqttQos.AT_MOST_ONCE)
                    .payload(payload)
                    .send()
                    .orTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                    .whenComplete { _, error ->
                        if (error != null) onPublishFailure(event, "$channel: ${error.javaClass.simpleName}")
                    }
            }
        } catch (exception: Exception) {
            onPublishFailure(event, exception.javaClass.simpleName)
        }
    }

    private fun onPublishFailure(event: OrderRealtimeEvent, reason: String) {
        publishFailures.increment()
        logger.warn("Realtime event {} for order {} was not published: {}", event.type, event.orderId, reason)
    }

    private fun refreshWriteKey() {
        try {
            // ttl 0 never expires; the key is only ever held in memory by the API.
            writeKey = requestKey(RealtimeChannels.ALL, type = "w", ttlSeconds = 0)
            logger.info("Realtime publishing is ready")
        } catch (exception: RealtimeUnavailableException) {
            logger.error("Could not obtain the realtime write key: {}", exception.message)
        }
    }

    private fun requestKey(channel: String, type: String, ttlSeconds: Long): String {
        if (!isConnected) throw RealtimeUnavailableException("Not connected to the broker")
        if (!keygenLock.tryLock(timeoutMs, TimeUnit.MILLISECONDS)) {
            throw RealtimeUnavailableException("Timed out waiting for the broker")
        }
        try {
            val pending = CompletableFuture<KeygenResponse>()
            pendingKeygen = pending
            // The request carries the secret key: it must never be logged.
            val request = mapOf(
                "key" to properties.emitter.secretKey,
                "channel" to channel,
                "type" to type,
                "ttl" to ttlSeconds
            )
            client.publishWith()
                .topic(KEYGEN_TOPIC)
                .qos(MqttQos.AT_MOST_ONCE)
                .payload(mapper.writeValueAsBytes(request))
                .send()
            val response = pending.get(timeoutMs, TimeUnit.MILLISECONDS)
            val key = response.key
            if (response.status != KEYGEN_OK || key.isNullOrEmpty() || response.channel != channel) {
                throw RealtimeUnavailableException(
                    "The broker refused to issue a key for $channel (status ${response.status}: ${response.message})"
                )
            }
            return key
        } catch (exception: RealtimeUnavailableException) {
            throw exception
        } catch (exception: Exception) {
            if (exception is InterruptedException) Thread.currentThread().interrupt()
            throw RealtimeUnavailableException("No key for $channel: ${exception.javaClass.simpleName}", exception)
        } finally {
            pendingKeygen = null
            keygenLock.unlock()
        }
    }

    private fun onBrokerMessage(publish: Mqtt3Publish) {
        when (publish.topic.toString()) {
            KEYGEN_TOPIC -> runCatching { mapper.readValue<KeygenResponse>(publish.payloadAsBytes) }
                .onSuccess { pendingKeygen?.complete(it) }
                .onFailure { pendingKeygen?.completeExceptionally(it) }

            ERROR_TOPIC -> {
                // Most likely a refused publish: ask for a fresh write key.
                publishFailures.increment()
                logger.warn("The realtime broker reported an error: {}", String(publish.payloadAsBytes))
                worker.execute(::refreshWriteKey)
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class KeygenResponse(
        val status: Int = 0,
        val key: String? = null,
        val channel: String? = null,
        val message: String? = null
    )

    companion object {
        const val PUBLISH_FAILURES_METRIC = "makalu.realtime.publish.failures"
        const val CONNECTED_METRIC = "makalu.realtime.connected"

        private const val KEYGEN_TOPIC = "emitter/keygen/"
        private const val ERROR_TOPIC = "emitter/error/"
        private const val KEYGEN_OK = 200
        private const val KEEP_ALIVE_SECONDS = 30
        private const val RECONNECT_INITIAL_DELAY_SECONDS = 1L
        private const val RECONNECT_MAX_DELAY_SECONDS = 30L
    }
}
