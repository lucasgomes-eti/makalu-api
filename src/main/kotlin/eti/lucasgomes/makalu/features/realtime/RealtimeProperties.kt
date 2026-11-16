package eti.lucasgomes.makalu.features.realtime

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.time.Duration

@ConfigurationProperties(prefix = "realtime")
@Component
data class RealtimeProperties(
    var brokerRequestTimeout: Duration = Duration.ofSeconds(2),
    var emitter: Emitter = Emitter()
) {
    data class Emitter(
        var privateHost: String = "",
        var privatePort: Int = 0,
        var publicUrl: String = "",
        var secretKey: String = ""
    ) {
        override fun toString(): String =
            "Emitter(privateHost=$privateHost, privatePort=$privatePort, publicUrl=$publicUrl, secretKey=<hidden>)"
    }
}
