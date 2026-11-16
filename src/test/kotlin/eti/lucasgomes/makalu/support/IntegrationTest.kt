package eti.lucasgomes.makalu.support

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.security.SecureRandom
import java.util.Base64
import java.util.HexFormat

@SpringBootTest
@AutoConfigureMockMvc
@Import(IntegrationTest.FakeRealtimeConfiguration::class)
abstract class IntegrationTest {
    @TestConfiguration
    class FakeRealtimeConfiguration {
        @Bean
        @Primary
        fun recordingRealtimeGateway(): RecordingRealtimeGateway = RecordingRealtimeGateway()
    }

    companion object {
        const val TEST_BROKER_PUBLIC_URL = "wss://broker.test/"
        private const val PORT_WHERE_NO_BROKER_LISTENS = 1
        private val postgres = PostgreSQLContainer(
            DockerImageName.parse("postgis/postgis:16-3.4").asCompatibleSubstituteFor("postgres")
        ).apply { start() }

        private fun randomBytes(size: Int) = ByteArray(size).also { SecureRandom().nextBytes(it) }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
            registry.add("cors.allowed-origins") { "http://localhost:3000" }
            registry.add("jwt.secret") { Base64.getEncoder().encodeToString(randomBytes(32)) }
            registry.add("ff3-cipher.key") { HexFormat.of().formatHex(randomBytes(16)) }
            registry.add("ff3-cipher.tweak") { HexFormat.of().formatHex(randomBytes(7)) }
            registry.add("realtime.emitter.private-host") { "localhost" }
            registry.add("realtime.emitter.private-port") { PORT_WHERE_NO_BROKER_LISTENS }
            registry.add("realtime.emitter.public-url") { TEST_BROKER_PUBLIC_URL }
            registry.add("realtime.emitter.secret-key") { "not-a-real-secret-key" }
        }
    }
}
