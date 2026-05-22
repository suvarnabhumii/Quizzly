package com.example.quizpas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizpas.databinding.ActivityAddQuestionBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddQuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddQuestionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Logika agar CheckBox hanya bisa dipilih SATU (seperti RadioButton)
        val answerLayouts = listOf(binding.layoutAns1, binding.layoutAns2, binding.layoutAns3, binding.layoutAns4)
        answerLayouts.forEach { layout ->
            layout.cbCorrect.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // Jika satu dicentang, yang lain otomatis mati
                    answerLayouts.forEach { if (it.cbCorrect != buttonView) it.cbCorrect.isChecked = false }
                }
            }
        }

        // 2. Tombol Close
        binding.btnClose.setOnClickListener { finish() }

        // 3. Tombol Simpan ke Laravel (Sudah diperbarui dari Firestore)
        binding.btnSaveQuestion.setOnClickListener {
            saveToLaravel()
        }

        // 4. Tombol Selanjutnya
        binding.btnNextStep.setOnClickListener {
            val intent = Intent(this, ReviewQuizActivity::class.java)
            startActivity(intent)
        }

        binding.btnAddOption.setOnClickListener {
            Toast.makeText(this, "Maksimal 4 pilihan jawaban", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToLaravel() {
        val questionText = binding.etQuestion.text.toString().trim()

        // Mengambil teks dari setiap include jawaban
        val ans1 = binding.layoutAns1.etAnswer.text.toString().trim()
        val ans2 = binding.layoutAns2.etAnswer.text.toString().trim()
        val ans3 = binding.layoutAns3.etAnswer.text.toString().trim()
        val ans4 = binding.layoutAns4.etAnswer.text.toString().trim()

        // Validasi input kosong
        if (questionText.isEmpty() || ans1.isEmpty() || ans2.isEmpty() || ans3.isEmpty() || ans4.isEmpty()) {
            Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        // Cek jawaban mana yang benar
        val correctAnswer = when {
            binding.layoutAns1.cbCorrect.isChecked -> ans1
            binding.layoutAns2.cbCorrect.isChecked -> ans2
            binding.layoutAns3.cbCorrect.isChecked -> ans3
            binding.layoutAns4.cbCorrect.isChecked -> ans4
            else -> null
        }

        if (correctAnswer == null) {
            Toast.makeText(this, "Pilih satu jawaban yang benar!", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Bungkus data ke dalam object data class QuestionRequest
        val optionsList = listOf(ans1, ans2, ans3, ans4)
        val requestData = QuestionRequest(
            question = questionText,
            options = optionsList,
            answer = correctAnswer
        )

        // 2. Inisialisasi Retrofit dengan IP laptop temanmu
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.238.116.243:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // 3. Jalankan pengiriman data (POST) ke server Laravel
        apiService.saveQuestion(requestData).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddQuestionActivity, "Soal kuis berhasil disimpan ke Laravel!", Toast.LENGTH_SHORT).show()
                    clearForm()
                } else {
                    Toast.makeText(this@AddQuestionActivity, "Gagal menyimpan: Kode ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@AddQuestionActivity, "Koneksi Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearForm() {
        binding.etQuestion.text.clear()
        val layouts = listOf(binding.layoutAns1, binding.layoutAns2, binding.layoutAns3, binding.layoutAns4)
        layouts.forEach {
            it.etAnswer.text.clear()
            it.cbCorrect.isChecked = false
        }
    }
}