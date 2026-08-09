package com.coba.crud.models.repository

import com.coba.crud.models.entity.PasswordResetToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, Long> {
    fun findByToken(token: String): PasswordResetToken?
    fun findByEmailOrderByCreatedAtDesc(email: String): List<PasswordResetToken>
}