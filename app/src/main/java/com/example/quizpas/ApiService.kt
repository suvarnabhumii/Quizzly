package com.example.quizpas

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    // 1. Jalur Simpan Soal Kuis
    @POST("api/save-question")
    fun saveQuestion(@Body data: QuestionRequest): Call<Void>

    // 2. Jalur Register Akun Baru
    @POST("api/register")
    fun register(@Body data: RegisterRequest): Call<AuthResponse>

    // 3. Jalur Login
    @POST("api/login")
    fun login(@Body data: LoginRequest): Call<AuthResponse>
}

// === DATA CLASS MODEL (PEMBUNGKUS DATA) ===

data class QuestionRequest(
    val question: String,
    val options: List<String>,
    val answer: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

// Menampung respons dari Laravel (misal token atau pesan sukses)
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String?
)