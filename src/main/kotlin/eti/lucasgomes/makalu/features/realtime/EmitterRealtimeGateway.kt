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
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

@Component
class EmitterRealtimeGateway(
    private val properties: RealtimeProperties,
    meterRegistry: MeterRegistry
) : RealtimeGateway {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val mapper = jacksonObjectMapper()
    private val brokerRequestTimeoutMs = properties.brokerRequestTimeout.toMillis()

    private val publishFailures: Counter = Counter.builder(PUBLISH_FAILURES_METRIC)
        .description("Order events that could not be handed to the broker")
        .register(meterRegistry)

    private val keyRequestThreadOffTheMqttEventLoop = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "realtime-keygen").apply { isDaemon = true }
    }

    private val oneKeyRequestAtATime = ReentrantLock()

    @Volatile
    private var keyRequestAwaitingBrokerAnswer: CompletableFuture<KeygenResponse>? = null

    @Volatile
    private var writeKeyOfCurrentConnection: String? = null

    private val client: Mqtt3AsyncClient = MqttClient.builder()
        .useMqttVersion3()
        .identifier("makalu-api-${UUID.randomUUID()}")
        .serverHost(properties.emitter.privateHost)
        .serverPort(properties.emitter.privatePort)
        .automaticReconnect()
        .initialDelay(RECONNECT_INITIAL_DELAY_SECONDS, TimeUnit.SECONDS)
        .maxDelay(RECONNECT_MAX_DELAY_SECONDS, TimeUnit.SECONDS)
        .applyAutomaticReconnect()
        .addConnectedListener {
            logger.info(
                "Connected to the realtime broker at {}:{}",
                properties.emitter.privateHost,
                properties.emitter.privatePort
            )
            keyRequestThreadOffTheMqttEventLoop.execute(::requestWriteKeyForNewConnection)
        }
        .addDisconnectedListener { context ->
            writeKeyOfCurrentConnection = null
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

    @PostConstruct
    fun startConnectingInBackground() {
        client.publishes(MqttGlobalPublishFilter.ALL, ::onUnsolicitedBrokerMessage)
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
    fun disconnect() {
        keyRequestThreadOffTheMqttEventLoop.shutdownNow()
        if (client.state.isConnectedOrReconnect) {
            client.disconnect()
        }
    }

    override fun issueReadOnlyKeyForExactChannel(channel: String, validity: Duration): String =
        requestKeyFromBroker(channel, permissions = READ_ONLY, validitySeconds = validity.seconds)

    override fun publishWithoutBlockingOrThrowing(event: OrderRealtimeEvent) {
        try {
            val writeKey = writeKeyOfCurrentConnection
            if (!isConnected || writeKey == null) {
                countAndLogPublishFailure(event, "not connected to the broker")
                return
            }
            val payload = mapper.writeValueAsBytes(event)
            val channels = listOf(
                RealtimeChannels.order(event.orderId.toLong()),
                RealtimeChannels.storeOrders(event.storeId.toLong())
            )
            channels.forEach { channel ->
                client.publishWith()
                    .topic("$writeKey/$channel")
                    .qos(MqttQos.AT_MOST_ONCE)
                    .payload(payload)
                    .send()
                    .orTimeout(brokerRequestTimeoutMs, TimeUnit.MILLISECONDS)
                    .whenComplete { _, error ->
                        if (error != null) countAndLogPublishFailure(event, "$channel: ${error.javaClass.simpleName}")
                    }
            }
        } catch (exception: Exception) {
            countAndLogPublishFailure(event, exception.javaClass.simpleName)
        }
    }

    private fun countAndLogPublishFailure(event: OrderRealtimeEvent, reason: String) {
        publishFailures.increment()
        logger.warn("Realtime event {} for order {} was not published: {}", event.type, event.orderId, reason)
    }

    private fun requestWriteKeyForNewConnection() {
        try {
            writeKeyOfCurrentConnection = requestKeyFromBroker(
                RealtimeChannels.EVERY_MAKALU_CHANNEL,
                permissions = WRITE_ONLY,
                validitySeconds = NEVER_EXPIRES
            )
            logger.info("Realtime publishing is ready")
        } catch (exception: RealtimeUnavailableException) {
            logger.error("Could not obtain the realtime write key: {}", exception.message)
        }
    }

    private fun requestKeyFromBroker(channel: String, permissions: String, validitySeconds: Long): String {
        if (!isConnected) throw RealtimeUnavailableException("Not connected to the broker")
        if (!oneKeyRequestAtATime.tryLock(brokerRequestTimeoutMs, TimeUnit.MILLISECONDS)) {
            throw RealtimeUnavailableException("Timed out waiting for the broker")
        }
        try {
            val brokerAnswer = CompletableFuture<KeygenResponse>()
            keyRequestAwaitingBrokerAnswer = brokerAnswer
            val requestContainingSecretKeyNeverToBeLogged = mapOf(
                "key" to properties.emitter.secretKey,
                "channel" to channel,
                "type" to permissions,
                "ttl" to validitySeconds
            )
            client.publishWith()
                .topic(KEYGEN_TOPIC)
                .qos(MqttQos.AT_MOST_ONCE)
                .payload(mapper.writeValueAsBytes(requestContainingSecretKeyNeverToBeLogged))
                .send()
            val response = brokerAnswer.get(brokerRequestTimeoutMs, TimeUnit.MILLISECONDS)
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
            keyRequestAwaitingBrokerAnswer = null
            oneKeyRequestAtATime.unlock()
        }
    }

    private fun onUnsolicitedBrokerMessage(publish: Mqtt3Publish) {
        when (publish.topic.toString()) {
            KEYGEN_TOPIC -> runCatching { mapper.readValue<KeygenResponse>(publish.payloadAsBytes) }
                .onSuccess { keyRequestAwaitingBrokerAnswer?.complete(it) }
                .onFailure { keyRequestAwaitingBrokerAnswer?.completeExceptionally(it) }

            ERROR_TOPIC_WHERE_REFUSED_PUBLISHES_ARE_REPORTED -> {
                publishFailures.increment()
                logger.warn("The realtime broker reported an error: {}", String(publish.payloadAsBytes))
                keyRequestThreadOffTheMqttEventLoop.execute(::requestWriteKeyForNewConnection)
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
        private const val ERROR_TOPIC_WHERE_REFUSED_PUBLISHES_ARE_REPORTED = "emitter/error/"
        private const val KEYGEN_OK = 200
        private const val READ_ONLY = "r"
        private const val WRITE_ONLY = "w"
        private const val NEVER_EXPIRES = 0L
        private const val KEEP_ALIVE_SECONDS = 30
        private const val RECONNECT_INITIAL_DELAY_SECONDS = 1L
        private const val RECONNECT_MAX_DELAY_SECONDS = 30L
    }
}
