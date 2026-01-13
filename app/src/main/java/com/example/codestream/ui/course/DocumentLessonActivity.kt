package com.example.codestream.ui.course

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codestream.data.db.AppDatabaseHelper
import com.example.codestream.databinding.ActivityDocumentLessonBinding
import com.example.codestream.utils.Constants
import java.net.URLEncoder

class DocumentLessonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDocumentLessonBinding
    private lateinit var db: AppDatabaseHelper

    private var userId: Long = -1L
    private var courseId: Long = -1L
    private var lessonId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabaseHelper(this)

        binding.toolbarDoc.setNavigationOnClickListener { finish() }

        userId = intent.getLongExtra(Constants.EXTRA_USER_ID, -1L)
        courseId = intent.getLongExtra(Constants.EXTRA_COURSE_ID, -1L)
        lessonId = intent.getLongExtra(Constants.EXTRA_LESSON_ID, -1L)

        val lesson = db.getLessonById(lessonId)
        if (lesson == null) {
            Toast.makeText(this, "Lesson tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvDocTitle.text = lesson.title

        val pdfUrl = lesson.contentUrl
        if (pdfUrl.isBlank()) {
            Toast.makeText(this, "URL PDF kosong", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.progressDoc.visibility = View.VISIBLE

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.webViewClient = WebViewClient()
        binding.webView.webChromeClient = WebChromeClient()

        val encoded = URLEncoder.encode(pdfUrl, "UTF-8")
        val googleViewer = "https://drive.google.com/viewerng/viewer?embedded=true&url=$encoded"

        binding.webView.loadUrl(googleViewer)

        // tombol selesai
        binding.btnFinishDoc.setOnClickListener {
            if (userId <= 0L || courseId <= 0L || lessonId <= 0L) {
                Toast.makeText(this, "Data lesson tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            db.setLessonDone(userId, courseId, lessonId, true)
            Toast.makeText(this, "Materi ditandai selesai ✅", Toast.LENGTH_SHORT).show()
            finish()
        }

        // hide progress setelah halaman mulai load
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                binding.progressDoc.visibility = View.GONE
            }
        }
    }
}
