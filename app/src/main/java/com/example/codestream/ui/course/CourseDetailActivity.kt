package com.example.codestream.ui.course

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codestream.data.db.AppDatabaseHelper
import com.example.codestream.data.db.SessionManager
import com.example.codestream.databinding.ActivityCourseDetailBinding
import com.example.codestream.ui.certificate.CertificateActivity
import com.example.codestream.utils.Constants

class CourseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourseDetailBinding
    private lateinit var db: AppDatabaseHelper
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
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

        binding.tvTitle.text = course.title
        binding.tvDesc.text = course.description
        binding.tvPrice.text = "Rp ${course.price}"

        // Wishlist
        fun refreshWishlistButton() {
            val wish = db.isWishlisted(session.userId(), courseId)
            binding.btnWishlist.text = if (wish) "Hapus Wishlist" else "Tambah Wishlist"
        }
        refreshWishlistButton()

        binding.btnWishlist.setOnClickListener {
            val nowWish = db.toggleWishlist(session.userId(), courseId)
            Toast.makeText(
                this,
                if (nowWish) "Ditambahkan ke wishlist" else "Dihapus dari wishlist",
                Toast.LENGTH_SHORT
            ).show()
            refreshWishlistButton()
        }

        // Buy / Go to Dashboard
        binding.btnBuy.setOnClickListener {
            if (db.isEnrolled(session.userId(), courseId)) {
                startActivity(Intent(this, CourseDashboardActivity::class.java).apply {
                    putExtra(Constants.EXTRA_COURSE_ID, courseId)
                })
            } else {
                startActivity(Intent(this, PaymentActivity::class.java).apply {
                    putExtra(Constants.EXTRA_COURSE_ID, courseId)
                })
            }
        }

        // tombol hanya muncul kalau user sudah enrolled
        val canClaim = db.isEnrolled(session.userId(), courseId)
        binding.btnClaimCertificate.visibility = if (canClaim) View.VISIBLE else View.GONE

        binding.btnClaimCertificate.setOnClickListener {
            // ini akan insert certificate kalau progress sudah 100%
            val ok = db.ensureCertificateIfEligible(session.userId(), courseId)

            if (ok) {
                Toast.makeText(this, "Sertifikat berhasil dibuat 🎉", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, CertificateActivity::class.java))
            } else {
                Toast.makeText(this, "Selesaikan course sampai 100% dulu ya", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
