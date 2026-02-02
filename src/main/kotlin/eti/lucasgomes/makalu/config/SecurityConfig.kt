package eti.lucasgomes.makalu.config

import eti.lucasgomes.makalu.features.auth.AuthErrorEntrypoint
import eti.lucasgomes.makalu.features.auth.JwtAuthFilter
import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.DispatcherTypeRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter,
    private val authErrorEntrypoint: AuthErrorEntrypoint
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            authorizeHttpRequests {
                authorize("/", permitAll)
                authorize("/auth/**", permitAll)
                authorize(method = HttpMethod.GET, pattern = "/profile/image/**", permitAll)
                authorize(DispatcherTypeRequestMatcher(DispatcherType.ERROR), permitAll)
                authorize(DispatcherTypeRequestMatcher(DispatcherType.FORWARD), permitAll)
                authorize(anyRequest, authenticated)
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthFilter)
            exceptionHandling { authenticationEntryPoint = authErrorEntrypoint }
        }
        return http.build()
    }
}