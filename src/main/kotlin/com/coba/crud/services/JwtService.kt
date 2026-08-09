package com.coba.crud.services

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtService {

    @Value("\${jwt.secret}")
    private lateinit var secret: String

    @Value("\${jwt.access-expiration}")
    private var accessExpirationMs: Long = 900000L

    private val secretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    // Generate access token
    fun generateAccessToken(email: String): String {
        return buildToken(email, accessExpirationMs)
    }

    private fun buildToken(email: String, expiration: Long): String {
        return Jwts.builder()
            .subject(email)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(secretKey)
            .compact()
    }

    // Ambil email dari token
    fun extractEmail(token: String): String {
        return getClaims(token).subject
    }

    // Validasi access token
    fun isTokenValid(token: String, email: String): Boolean {
        return extractEmail(token) == email && !isTokenExpired(token)
    }

    fun isTokenExpired(token: String): Boolean {
        return getClaims(token).expiration.before(Date())
    }

    private fun getClaims(token: String) = Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .payload
}