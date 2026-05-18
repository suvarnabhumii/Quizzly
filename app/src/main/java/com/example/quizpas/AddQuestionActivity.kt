package com.example.quizpas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizpas.databinding.ActivityAddQuestionBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddQuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddQuestionBinding
    // Inisialisasi Firestore
    private val db = FirebaseFirestore.getInstance()

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

        // 3. Tombol Simpan ke Firestore
        binding.btnSaveQuestion.setOnClickListener {
            saveToFirestore()
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

    private fun saveToFirestore() {
        val questionText = binding.etQuestion.text.toString().trim()

        // Mengambil teks dari setiap include
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

        // Bungkus data ke dalam Map
        val questionData = hashMapOf(
            "question" to questionText,
            "options" to listOf(ans1, ans2, ans3, ans4),
            "answer" to correctAnswer,
            "timestamp" to System.currentTimeMillis()
        )

        // Kirim ke Firestore (Koleksi "questions")
        db.collection("questions")
            .add(questionData)
            .addOnSuccessListener {
                Toast.makeText(this, "Berhasil simpan ke Firebase!", Toast.LENGTH_SHORT).show()
                clearForm()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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