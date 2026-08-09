package com.coba.crud.services

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisSessionService(
    private val redisTemplate: StringRedisTemplate
) {
    companion object {
        private const val SESSION_PREFIX = "session:"
    }

    // Simpan sesi aktif saat login
    fun saveSession(email: String, ttlDays: Long = 1) {
        redisTemplate.opsForValue().set(
            "$SESSION_PREFIX$email",
            "active",
            Duration.ofDays(ttlDays)
        )
    }

    // Cek apakah sesi masih aktif
    fun isSessionActive(email: String): Boolean {
        return redisTemplate.hasKey("$SESSION_PREFIX$email") == true
    }

    // Hapus sesi saat logout
    fun deleteSession(email: String) {
        redisTemplate.delete("$SESSION_PREFIX$email")
    }
}