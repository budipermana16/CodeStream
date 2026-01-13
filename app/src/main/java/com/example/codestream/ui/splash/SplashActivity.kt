package com.example.codestream.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.codestream.data.db.SessionManager
import com.example.codestream.databinding.ActivitySplashBinding
import com.example.codestream.ui.auth.LoginActivity
import com.example.codestream.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val next = if (session.isLoggedIn()) MainActivity::class.java else LoginActivity::class.java
        startActivity(Intent(this, next))
        finish()
    }
}
