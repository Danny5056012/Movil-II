package com.example.autodrivemanagermvvm.data.model

import com.google.gson.annotations.SerializedName

data class CarDto(
    val id: Int,
    val make: String,
    val year: Int,
    val model: String,
    val speed: Int,
    val fuel: Int
)

data class CarCreateDto(
    val make: String,
    val year: Int,
    val model: String,
    val speed: Int,
    val fuel: Int
)

data class CarUiModel(
    val id: Int,
    val make: String,
    val year: Int,
    val model: String,
    val speed: Int,
    val fuel: Int,
    val imageResId: Int
)

data class AuthRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    @SerializedName("token") val token: String? = null,
    @SerializedName("access") val access: String? = null
) {
    fun extractToken(): String? = if (!token.isNullOrBlank()) token else access
}

data class UserRegisterRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String
)

data class UserRegisterResponse(
    val first_name: String,
    val last_name: String,
    val email: String
)

