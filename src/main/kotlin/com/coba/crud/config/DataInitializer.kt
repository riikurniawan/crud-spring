package com.coba.crud.config

import com.coba.crud.models.entity.User
import com.coba.crud.models.repository.UserRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        println(">>> [DEBUG] DataInitializer running...")
        
        val existing = userRepository.findByEmail("user.testing@xyz.com")
        if (existing == null) {
            val user = User(
                fullName = "User Testing",
                email = "user.testing@xyz.com",
                password = passwordEncoder.encode("1234567890"),
                createdAt = LocalDateTime.now(),
                isDeleted = 0
            )
            userRepository.save(user)
            println(">>> [DEBUG] User created: ${user.email} / 1234567890")
        } else {
            println(">>> [DEBUG] User sudah ada, email: ${existing.email}")
        }
    }}