package com.coba.crud.dtos.request

data class ResetPasswordRequestDto(
    val token: String,
    val newPassword: String
)