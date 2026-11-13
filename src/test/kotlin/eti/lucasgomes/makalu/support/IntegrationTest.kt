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

/**
 * Base for tests that need the whole application: a real PostGIS database (the schema
 * uses spatial types and native queries) and a recording stand-in for the broker.
 */
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
        // Started once and shared by every test class; Testcontainers removes it at JVM exit.
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
            // Generated per run: tests never need a stable signing key.
            registry.add("jwt.secret") { Base64.getEncoder().encodeToString(randomBytes(32)) }
            registry.add("ff3-cipher.key") { HexFormat.of().formatHex(randomBytes(16)) }
            registry.add("ff3-cipher.tweak") { HexFormat.of().formatHex(randomBytes(7)) }
        }
    }
}
