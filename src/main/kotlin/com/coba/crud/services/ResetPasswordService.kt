package com.coba.crud.services

import com.coba.crud.models.entity.PasswordResetToken
import com.coba.crud.models.repository.PasswordResetTokenRepository
import com.coba.crud.models.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class ResetPasswordService(
    private val userRepository: UserRepository,
    private val tokenRepository: PasswordResetTokenRepository,
    private val mailSender: JavaMailSender,
    private val passwordEncoder: PasswordEncoder
) {

    @Value("\${app.base-url}")
    private lateinit var baseUrl: String

    // Step 1 — Kirim email reset password
    fun sendResetEmail(email: String): Boolean {
        // Cek user ada atau tidak
        val user = userRepository.findByEmailAndIsDeleted(email, 0)
            ?: return false

        // Invalidate token lama kalau ada
        val oldTokens = tokenRepository.findByEmailOrderByCreatedAtDesc(email)
        oldTokens.forEach { old ->
            tokenRepository.save(old.copy(used = true))
        }

        // Generate token baru
        val token = UUID.randomUUID().toString()
        val resetToken = PasswordResetToken(
            email = email,
            token = token,
            expiredAt = LocalDateTime.now().plusHours(1), // expired 1 jam
            used = false
        )
        tokenRepository.save(resetToken)

        // Kirim email
        val resetLink = "$baseUrl/auth/reset-password?token=$token"
        val message = SimpleMailMessage()
        message.setTo(email)
        message.subject = "Reset Password"
        message.text = """
            Halo ${user.fullName},
            
            Kamu menerima email ini karena ada permintaan reset password untuk akun kamu.
            
            Klik link berikut untuk reset password (berlaku 1 jam):
            $resetLink
            
            Jika kamu tidak merasa melakukan permintaan ini, abaikan email ini.
        """.trimIndent()

        mailSender.send(message)
        return true
    }

    // Step 2 — Reset password dengan token
    fun resetPassword(token: String, newPassword: String): String {
        val resetToken = tokenRepository.findByToken(token)
            ?: return "Token tidak valid"

        if (resetToken.used) {
            return "Token sudah digunakan"
        }

        if (resetToken.expiredAt.isBefore(LocalDateTime.now())) {
            return "Token sudah expired"
        }

        val user = userRepository.findByEmailAndIsDeleted(resetToken.email, 0)
            ?: return "User tidak ditemukan"

        // Update password
        userRepository.save(user.copy(
            password = passwordEncoder.encode(newPassword),
            updatedAt = LocalDateTime.now()
        ))

        // Tandai token sudah digunakan
        tokenRepository.save(resetToken.copy(used = true))

        return "ok"
    }
}