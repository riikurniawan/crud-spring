package com.coba.crud.models.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_tokens")
data class RefreshToken(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "email", nullable = false)
    val email: String = "",

    @Column(name = "token", nullable = false, unique = true)
    val token: String = "",

    @Column(name = "expired_at", nullable = false)
    val expiredAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "revoked")
    val revoked: Boolean = false,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)