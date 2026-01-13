package com.example.codestream.ui.certificate

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codestream.data.db.AppDatabaseHelper
import com.example.codestream.data.db.SessionManager
import com.example.codestream.databinding.ActivityCertificateBinding

class CertificateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCertificateBinding
    private lateinit var db: AppDatabaseHelper
    private lateinit var session: SessionManager

    private val adapter = CertificateAdapter { cert ->
        val i = Intent(this, CertificateDetailActivity::class.java).apply {
            putExtra(CertificateDetailActivity.EXTRA_USER_NAME, resolveUserName())
            putExtra(CertificateDetailActivity.EXTRA_COURSE_TITLE, cert.courseTitle)
            putExtra(CertificateDetailActivity.EXTRA_ISSUED_AT, cert.issuedAt)
        }
        startActivity(i)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertificateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabaseHelper(this)
        session = SessionManager(this)

        binding.toolbarCertificates.setNavigationOnClickListener { finish() }

        binding.rvCertificates.layoutManager = LinearLayoutManager(this)
        binding.rvCertificates.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadCertificates()
    }

    private fun loadCertificates() {
        val userId = session.userId()

        val list = try {
            if (userId <= 0L) emptyList()
            else db.getCertificates(userId) // ✅ ambil dari AppDatabaseHelper
        } catch (e: Throwable) {
            e.printStackTrace()
            emptyList()
        }

        adapter.submit(list)

        val isEmpty = list.isEmpty()
        binding.rvCertificates.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.emptyStateCard.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun resolveUserName(): String {
        val userId = session.userId()
        val user = try { db.getUser(userId) } catch (e: Throwable) { null }
        return user?.name ?: "User"
    }
}
