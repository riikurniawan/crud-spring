package com.coba.crud.dtos.response

data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val email: String,
    val fullName: String
)