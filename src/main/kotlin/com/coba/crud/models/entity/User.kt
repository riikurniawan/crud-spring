package com.coba.crud.models.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import java.time.LocalDateTime

@Entity
@Table(name = "portal_user_db", schema = "public")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    val id: Long = 0,

    @Column(name = "full_name", nullable = false)
    val fullName: String = "",

    @Column(name = "email", nullable = false, unique = true)
    val email: String = "",

    @Column(name = "password")
    val password: String? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime? = null,

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,

    @Column(name = "deleted_at")
    val deletedAt: LocalDateTime? = null,

    @Column(name = "is_deleted")
    val isDeleted: Int = 0
)