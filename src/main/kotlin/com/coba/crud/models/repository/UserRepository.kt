package com.coba.crud.models.repository

import com.coba.crud.models.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByEmailAndIsDeleted(email: String, isDeleted: Int = 0): User?
}