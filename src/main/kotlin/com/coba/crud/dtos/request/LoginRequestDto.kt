package com.coba.crud.dtos.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequestDto(
    @field:Email(message = "Format email tidak valid")
    @field:NotBlank(message = "Email tidak boleh kosong")
    val email: String,

    @field:NotBlank(message = "Password tidak boleh kosong")
    val password: String
)