package com.example.codestream.ui.certificate

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codestream.databinding.ActivityCertificateDetailBinding

class CertificateDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCertificateDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertificateDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarCertificateDetail.setNavigationOnClickListener { finish() }

        val userName = intent.getStringExtra(EXTRA_USER_NAME) ?: "User"
        val courseTitle = intent.getStringExtra(EXTRA_COURSE_TITLE) ?: "-"
        val issuedAt = intent.getStringExtra(EXTRA_ISSUED_AT) ?: "-"

        // Pastikan ID ini ada di XML detail
        binding.tvName.text = userName
        binding.tvCourse.text = courseTitle
        binding.tvDate.text = issuedAt

        binding.btnDownload.setOnClickListener {
            val ok = CertificateUtils.saveCertificateToGallery(
                context = this,
                userName = userName,
                courseTitle = courseTitle,
                issuedAt = issuedAt
            )
            Toast.makeText(
                this,
                if (ok) "Certificate tersimpan di Galeri" else "Gagal menyimpan certificate",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnShare.setOnClickListener {
            val uri = CertificateUtils.createTempCertificateFile(
                context = this,
                userName = userName,
                courseTitle = courseTitle,
                issuedAt = issuedAt
            )
            if (uri != null) {
                CertificateUtils.shareImage(this, uri)
            } else {
                Toast.makeText(this, "Gagal membuat file untuk share", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val EXTRA_USER_NAME = "EXTRA_USER_NAME"
        const val EXTRA_COURSE_TITLE = "EXTRA_COURSE_TITLE"
        const val EXTRA_ISSUED_AT = "EXTRA_ISSUED_AT"
    }
}
