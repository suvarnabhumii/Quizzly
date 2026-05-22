package com.example.quizpas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizpas.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Tombol Kembali (Panah Kiri)
        binding.btnBack.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya
        }

        // 2. Tombol Simpan Perubahan
        binding.btnSave.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Validasi dasar agar input nama dan email tidak kosong
            if (username.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Nama Pengguna dan Email tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Logika pengecekan password baru
            if (password.isNotEmpty()) {
                // Di sini nanti tempat kamu masukan fungsi update data + password ke Laravel
                Toast.makeText(this, "Profil dan Password berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            } else {
                // Di sini nanti tempat kamu masukan fungsi update data tanpa ganti password
                Toast.makeText(this, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            }

            // Tutup halaman edit setelah simpan
            finish()
        }
    }
}