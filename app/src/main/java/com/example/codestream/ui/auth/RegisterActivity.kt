package com.example.codestream.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codestream.data.db.AppDatabaseHelper
import com.example.codestream.databinding.ActivityRegisterBinding
import com.example.codestream.utils.Validator

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db: AppDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabaseHelper(this)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text?.toString().orEmpty()
            val email = binding.etEmail.text?.toString().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()

            if (name.trim().length < 2) {
                binding.tilName.error = "Nama minimal 2 karakter"
                return@setOnClickListener
            } else binding.tilName.error = null

            if (!Validator.isValidEmail(email)) {
                binding.tilEmail.error = "Email tidak valid"
                return@setOnClickListener
            } else binding.tilEmail.error = null

            if (!Validator.isValidPassword(password)) {
                binding.tilPassword.error = "Password minimal 6 karakter"
                return@setOnClickListener
            } else binding.tilPassword.error = null

            val id = db.register(name, email, password)
            if (id == -1L) {
                Toast.makeText(this, "Gagal daftar. Email mungkin sudah terpakai.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Berhasil daftar. Silakan login.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
