package eti.lucasgomes.makalu.features.realtime

import eti.lucasgomes.makalu.support.IntegrationTest
import io.micrometer.core.instrument.MeterRegistry
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class StartupWithoutBrokerTest : IntegrationTest() {

    @Autowired
    private lateinit var emitterGateway: EmitterRealtimeGateway

    @Autowired
    private lateinit var meterRegistry: MeterRegistry

    @Test
    fun `the application starts although the broker is unreachable`() {
        assertFalse(emitterGateway.isConnected)
        assertEquals(0.0, meterRegistry.get(EmitterRealtimeGateway.CONNECTED_METRIC).gauge().value())
        assertNotNull(meterRegistry.find(EmitterRealtimeGateway.PUBLISH_FAILURES_METRIC).counter())
    }
}
