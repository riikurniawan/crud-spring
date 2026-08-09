package com.coba.crud.config

import com.coba.crud.services.JwtService
import com.coba.crud.services.RedisSessionService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
    private val redisSessionService: RedisSessionService  // ← ganti refreshTokenRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7)

        try {
            val email = jwtService.extractEmail(token)

            if (SecurityContextHolder.getContext().authentication == null) {

                // Cek sesi di Redis (sangat cepat, tidak query DB)
                if (!redisSessionService.isSessionActive(email)) {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Sesi sudah berakhir, silakan login ulang")
                    return
                }

                val userDetails = userDetailsService.loadUserByUsername(email)

                if (jwtService.isTokenValid(token, email)) {
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token tidak valid")
                    return
                }
            }
        } catch (e: io.jsonwebtoken.ExpiredJwtException) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Access token expired")
            return
        } catch (e: Exception) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token tidak valid")
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun sendErrorResponse(response: HttpServletResponse, status: Int, message: String) {
        response.status = status
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        val body = ObjectMapper().writeValueAsString(mapOf("message" to message))
        response.writer.write(body)
    }
}