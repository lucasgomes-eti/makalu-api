package eti.lucasgomes.makalu.features.realtime

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.time.Duration

@ConfigurationProperties(prefix = "realtime")
@Component
data class RealtimeProperties(
    /** Off by default, so the API runs without a broker. */
    var enabled: Boolean = false,
    /** Upper bound for a publish and for a key request. */
    var timeout: Duration = Duration.ofSeconds(2),
    var emitter: Emitter = Emitter()
) {
    data class Emitter(
        /** Broker host as the API reaches it (private network). */
        var host: String = "localhost",
        var port: Int = 8090,
        /** The URL handed to clients, e.g. wss://broker.example/. */
        var publicUrl: String = "ws://localhost:8090/",
        /** The broker's secret (master) key. Used only to request channel keys. */
        var secretKey: String = ""
    ) {
        // The default toString of a data class would put the secret key in a log line.
        override fun toString(): String = "Emitter(host=$host, port=$port, publicUrl=$publicUrl)"
    }
}
