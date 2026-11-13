package eti.lucasgomes.makalu.features.realtime

import eti.lucasgomes.makalu.support.IntegrationTest
import io.micrometer.core.instrument.MeterRegistry
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

/** The application must start, and stay usable, with realtime on and no broker to talk to. */
@TestPropertySource(properties = ["realtime.enabled=true", "realtime.emitter.host=localhost", "realtime.emitter.port=1"])
class RealtimeEnabledStartupTest : IntegrationTest() {

    @Autowired
    private lateinit var gateways: List<RealtimeGateway>

    @Autowired
    private lateinit var meterRegistry: MeterRegistry

    @Test
    fun `the application starts with the emitter gateway although the broker is unreachable`() {
        val emitter = gateways.filterIsInstance<EmitterRealtimeGateway>().single()

        assertFalse(emitter.isConnected)
        assertFalse(gateways.any { it is DisabledRealtimeGateway })
        assertEquals(0.0, meterRegistry.get(EmitterRealtimeGateway.CONNECTED_METRIC).gauge().value())
        assertNotNull(meterRegistry.find(EmitterRealtimeGateway.PUBLISH_FAILURES_METRIC).counter())
    }
}
