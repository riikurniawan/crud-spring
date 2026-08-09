package com.coba.crud.services

import com.coba.crud.models.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmailAndIsDeleted(username, 0)
            ?: throw UsernameNotFoundException("User tidak ditemukan: $username")

        return User.builder()
            .username(user.email)
            .password(user.password ?: "")
            .roles("USER")
            .build()
    }
}