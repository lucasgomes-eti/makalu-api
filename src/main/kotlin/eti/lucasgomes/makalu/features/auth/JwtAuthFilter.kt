package eti.lucasgomes.makalu.features.auth

import eti.lucasgomes.makalu.features.auth.model.AuthError
import eti.lucasgomes.makalu.shared.exceptions.AuthErrorException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val pathsToSkip = listOf("/auth/register", "/auth/login", "/auth/refresh")
        if (pathsToSkip.contains(request.servletPath)) {
            filterChain.doFilter(request, response)
            return
        }
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            if (jwtService.isAccessTokenValid(authHeader)) {
                val userId = jwtService.getUserIdFromToken(authHeader)
                val user = userRepository.findById(userId)
                    .orElseThrow { AuthErrorException(AuthError.InvalidToken) }
                val userDetails = MakaluUserDetails(user)
                val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                SecurityContextHolder.getContext().authentication = auth
            }
        }
        filterChain.doFilter(request, response)
    }
}