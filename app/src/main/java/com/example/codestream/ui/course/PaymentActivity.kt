package com.example.codestream.ui.course

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codestream.data.db.AppDatabaseHelper
import com.example.codestream.data.db.SessionManager
import com.example.codestream.databinding.ActivityPaymentBinding
import com.example.codestream.utils.Constants
import java.time.LocalDateTime

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var db: AppDatabaseHelper
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabaseHelper(this)
        session = SessionManager(this)

        val courseId = intent.getLongExtra(Constants.EXTRA_COURSE_ID, -1L)
        val course = db.getCourse(courseId)
        if (course == null) {
            Toast.makeText(this, "Course tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvTitle.text = "Course: ${course.title}"
        binding.tvAmount.text = "Total: Rp ${course.price}"

        binding.btnPay.setOnClickListener {
            val ok = db.enroll(session.userId(), courseId, LocalDateTime.now().toString())
            Toast.makeText(this, if (ok) "Pembayaran sukses" else "Sudah pernah membeli course ini", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, CourseDashboardActivity::class.java).apply {
                putExtra(Constants.EXTRA_COURSE_ID, courseId)
            })
            finish()
        }
    }
}
