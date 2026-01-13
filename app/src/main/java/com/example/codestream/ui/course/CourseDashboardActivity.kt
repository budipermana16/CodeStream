package com.example.codestream.ui.course

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codestream.data.db.AppDatabaseHelper
import com.example.codestream.data.db.SessionManager
import com.example.codestream.databinding.ActivityCourseDashboardBinding
import com.example.codestream.ui.certificate.CertificateActivity
import com.example.codestream.utils.Constants

class CourseDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourseDashboardBinding
    private lateinit var db: AppDatabaseHelper
    private lateinit var session: SessionManager
    private lateinit var adapter: LessonAdapter

    private var courseId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabaseHelper(this)
        session = SessionManager(this)

        courseId = intent.getLongExtra(Constants.EXTRA_COURSE_ID, -1L)

        val course = db.getCourse(courseId)
        if (course == null) {
            Toast.makeText(this, "Course tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvTitle.text = course.title

        // Setup RecyclerView
        adapter = LessonAdapter { lesson ->
            val userId = session.userId()

            val cls = if (lesson.type == "video") {
                VideoLessonActivity::class.java
            } else {
                DocumentLessonActivity::class.java
            }

            startActivity(Intent(this, cls).apply {
                putExtra(Constants.EXTRA_USER_ID, userId)
                putExtra(Constants.EXTRA_COURSE_ID, courseId)
                putExtra(Constants.EXTRA_LESSON_ID, lesson.id)
            })
        }

        binding.rvLessons.layoutManager = LinearLayoutManager(this)
        binding.rvLessons.adapter = adapter

        // Default button state
        binding.btnClaimCertificate.visibility = View.GONE

        // Tombol "Selesaikan Kelas" (untuk menandai selesai)
        binding.btnFinishCourse.setOnClickListener {
            // butuh fungsi ini di SessionManager (lihat catatan di bawah)
            session.setCourseCompleted(courseId, true)
            Toast.makeText(this, "Kelas ditandai selesai ✅", Toast.LENGTH_SHORT).show()
            refreshProgressUI()
        }

        // Tombol "Ambil Sertifikat"
        binding.btnClaimCertificate.setOnClickListener {
            val userIdLong = session.userId().toLong()
            val issuedAt = System.currentTimeMillis().toString()
            val courseTitle = course.title

            val ok = db.ensureCertificateIfEligible(session.userId(), courseId)
            if (ok) {
                Toast.makeText(this, "Sertifikat berhasil dibuat 🎉", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, CertificateActivity::class.java))
            } else {
                Toast.makeText(this, "Selesaikan course sampai 100% dulu ya", Toast.LENGTH_SHORT).show()
            }

            Toast.makeText(this, "Sertifikat berhasil dibuat 🎉", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, CertificateActivity::class.java))
        }

        // Initial UI
        refreshProgressUI()
    }

    override fun onResume() {
        super.onResume()

        // load lessons
        val lessons = db.getLessons(session.userId(), courseId)
        adapter.submit(lessons)

        // refresh progress & button visibility
        refreshProgressUI()
    }

    private fun refreshProgressUI() {
        // 1) ambil progress dari DB kalau kamu punya fungsi ini
        // 2) kalau belum/0 terus, bisa pakai fallback "course completed" dari session
        val completedByButton = session.isCourseCompleted(courseId)

        val progressFromDb = try {
            db.getCourseProgressPercent(session.userId(), courseId)
        } catch (e: Throwable) {
            0
        }

        val progress = if (completedByButton) 100 else progressFromDb

        binding.tvProgress.text = "Progress: $progress%"
        binding.progressBar.progress = progress

        binding.btnClaimCertificate.visibility = if (progress >= 100) View.VISIBLE else View.GONE
    }
}
