package com.example.codestream.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codestream.data.db.AppDatabaseHelper
import com.example.codestream.data.db.SessionManager
import com.example.codestream.databinding.ActivityLoginBinding
import com.example.codestream.ui.main.MainActivity
import com.example.codestream.utils.Validator

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: AppDatabaseHelper
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabaseHelper(this)
        session = SessionManager(this)

        if (session.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()

            if (!Validator.isValidEmail(email)) {
                binding.tilEmail.error = "Email tidak valid"
                return@setOnClickListener
            } else binding.tilEmail.error = null

            if (!Validator.isValidPassword(password)) {
                binding.tilPassword.error = "Password minimal 6 karakter"
                return@setOnClickListener
            } else binding.tilPassword.error = null

            val user = db.login(email, password)
            if (user == null) {
                Toast.makeText(this, "Email / password salah", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            session.setLoggedIn(user.id, user.email)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
