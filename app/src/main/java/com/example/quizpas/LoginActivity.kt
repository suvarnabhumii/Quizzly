package com.example.quizpas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizpas.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Inisialisasi Retrofit menggunakan IP laptop temanmu
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.238.116.243:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // --- LOGIKA TOMBOL MASUK ---
        binding.btnLogin.setOnClickListener {
            // 1. Ambil teks dari input
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // 2. Validasi: Cek jika kosong
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Mohon isi email dan kata sandi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginData = LoginRequest(email, password)

            // 3. Tembak data login ke Laravel
            apiService.login(loginData).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                        // 4. Pindah ke HomeActivity jika sukses
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)

                        // Tetap mempertahankan logika ambil Nama dari email sebelum karakter '@'
                        val userName = if (email.contains("@")) email.substringBefore("@") else email
                        intent.putExtra("USER_NAME", userName)

                        startActivity(intent)
                        finish() // Tutup Login agar tidak bisa balik lagi
                    } else {
                        Toast.makeText(this@LoginActivity, "Login Gagal: Email atau Password salah", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Error Koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // --- LOGIKA LINK DAFTAR (Belum punya akun?) ---
        binding.tvRegisterLink.setOnClickListener {
            // Pindah ke halaman Register
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}