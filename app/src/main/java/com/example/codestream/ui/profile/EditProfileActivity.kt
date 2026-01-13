package com.example.codestream.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codestream.data.db.AppDatabaseHelper
import com.example.codestream.data.db.SessionManager
import com.example.codestream.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var db: AppDatabaseHelper
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabaseHelper(this)
        session = SessionManager(this)

        val userId = session.userId()
        if (userId <= 0) {
            Toast.makeText(this, "Session tidak valid, login ulang.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val user = db.getUser(userId)
        if (user == null) {
            Toast.makeText(this, "User tidak ditemukan.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)

        binding.btnSave.setOnClickListener {

            val newName = binding.etName.text?.toString()?.trim().orEmpty()
            val newEmail = binding.etEmail.text?.toString()?.trim().orEmpty()
            val newPhone = binding.etPhone.text?.toString()?.trim().orEmpty()
            val newBio = binding.etBio.text?.toString()?.trim().orEmpty()

            if (newName.isBlank() || newEmail.isBlank()) {
                Toast.makeText(this, "Nama & Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ok = db.updateProfile(
                userId = userId,
                name = newName,
                email = newEmail,
                phone = newPhone,
                bio = newBio
            )

            if (ok) {
                Toast.makeText(this, "Profile berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Gagal menyimpan profile (email mungkin sudah dipakai)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
