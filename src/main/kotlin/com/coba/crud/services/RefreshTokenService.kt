package com.coba.crud.services

import com.coba.crud.models.entity.RefreshToken
import com.coba.crud.models.repository.RefreshTokenRepository
import com.coba.crud.models.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository,
    private val redisSessionService: RedisSessionService,
    private val jwtService: JwtService
) {

    fun createRefreshToken(email: String): RefreshToken {
        revokeAllUserTokens(email)
        val refreshToken = RefreshToken(
            email = email,
            token = UUID.randomUUID().toString(),
            expiredAt = LocalDateTime.now().plusDays(1)
        )
        val saved = refreshTokenRepository.save(refreshToken)
        redisSessionService.saveSession(email, ttlDays = 1)
        return saved
    }

    fun refreshAccessToken(token: String): Map<String, String> {
        val refreshToken = refreshTokenRepository.findByToken(token)
            ?: return mapOf("error" to "Refresh token tidak valid")

        if (refreshToken.revoked) {
            return mapOf("error" to "Refresh token sudah direvoke")
        }

        if (refreshToken.expiredAt.isBefore(LocalDateTime.now())) {
            return mapOf("error" to "Refresh token sudah expired, silakan login ulang")
        }

        val user = userRepository.findByEmailAndIsDeleted(refreshToken.email, 0)
            ?: return mapOf("error" to "User tidak ditemukan")

        val newAccessToken = jwtService.generateAccessToken(user.email)
        return mapOf(
            "accessToken" to newAccessToken,
            "refreshToken" to token
        )
    }

    fun revokeAllUserTokens(email: String) {
        val tokens = refreshTokenRepository.findByEmailOrderByCreatedAtDesc(email)
        tokens.forEach { token ->
            refreshTokenRepository.save(token.copy(revoked = true))
        }
        redisSessionService.deleteSession(email)
    }
}