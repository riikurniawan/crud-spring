package com.coba.crud.controllers

import com.coba.crud.dtos.response.AuthResponseDto
import com.coba.crud.dtos.request.ForgotPasswordRequestDto
import com.coba.crud.dtos.request.LoginRequestDto
import com.coba.crud.dtos.request.RefreshTokenRequestDto
import com.coba.crud.dtos.request.ResetPasswordRequestDto
import com.coba.crud.models.repository.UserRepository
import com.coba.crud.services.JwtService
import com.coba.crud.services.RefreshTokenService
import com.coba.crud.services.ResetPasswordService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
    private val userRepository: UserRepository,
    private val resetPasswordService: ResetPasswordService,
    private val refreshTokenService: RefreshTokenService
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequestDto): ResponseEntity<Any> {
        return try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )

            val user = userRepository.findByEmailAndIsDeleted(request.email, 0)
                ?: return ResponseEntity.status(404).body(mapOf("message" to "User tidak ditemukan"))

            val accessToken = jwtService.generateAccessToken(user.email)
            val refreshToken = refreshTokenService.createRefreshToken(user.email)

            ResponseEntity.ok(
                AuthResponseDto(
                    accessToken = accessToken,
                    refreshToken = refreshToken.token,
                    email = user.email,
                    fullName = user.fullName
                )
            )
        } catch (e: AuthenticationException) {
            ResponseEntity.status(401).body(mapOf("message" to "Email atau password salah"))
        }
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody request: RefreshTokenRequestDto): ResponseEntity<Any> {
        val result = refreshTokenService.refreshAccessToken(request.refreshToken)

        return if (result.containsKey("error")) {
            ResponseEntity.status(401).body(mapOf("message" to result["error"]))
        } else {
            ResponseEntity.ok(result)
        }
    }

    @PostMapping("/logout")
    fun logout(@RequestBody request: RefreshTokenRequestDto): ResponseEntity<Any> {
        val refreshToken = request.refreshToken
        val token = refreshTokenService.refreshAccessToken(refreshToken)

        // Ambil email dari refresh token lalu revoke semua token
        val claims = try {
            jwtService.extractEmail(refreshToken)
        } catch (e: Exception) {
            return ResponseEntity.status(401).body(mapOf("message" to "Refresh token tidak valid"))
        }

        refreshTokenService.revokeAllUserTokens(claims)
        return ResponseEntity.ok(mapOf("message" to "Logout berhasil"))
    }

    @PostMapping("/forgot-password")
    fun forgotPassword(@RequestBody request: ForgotPasswordRequestDto): ResponseEntity<Any> {
        resetPasswordService.sendResetEmail(request.email)
        return ResponseEntity.ok(mapOf("message" to "Jika email terdaftar, link reset password akan dikirim"))
    }

    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody request: ResetPasswordRequestDto): ResponseEntity<Any> {
        val result = resetPasswordService.resetPassword(request.token, request.newPassword)

        return if (result == "ok") {
            ResponseEntity.ok(mapOf("message" to "Password berhasil direset"))
        } else {
            ResponseEntity.badRequest().body(mapOf("message" to result))
        }
    }
}