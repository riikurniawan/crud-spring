package com.coba.crud.models.repository

import com.coba.crud.models.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByToken(token: String): RefreshToken?
    fun findByEmailOrderByCreatedAtDesc(email: String): List<RefreshToken>
}