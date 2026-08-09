package com.coba.crud.models.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "password_reset_tokens")
data class PasswordResetToken(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "email", nullable = false)
    val email: String = "",

    @Column(name = "token", nullable = false, unique = true)
    val token: String = "",

    @Column(name = "expired_at", nullable = false)
    val expiredAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "used")
    val used: Boolean = false,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)