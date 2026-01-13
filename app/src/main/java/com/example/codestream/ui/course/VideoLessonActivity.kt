package com.example.codestream.ui.course

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codestream.data.db.AppDatabaseHelper
import com.example.codestream.databinding.ActivityVideoLessonBinding
import com.example.codestream.utils.Constants

class VideoLessonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoLessonBinding
    private lateinit var db: AppDatabaseHelper

    private var userId: Long = -1L
    private var courseId: Long = -1L
    private var lessonId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabaseHelper(this)

        binding.toolbarVideo.setNavigationOnClickListener { finish() }

        userId = intent.getLongExtra(Constants.EXTRA_USER_ID, -1L)
        courseId = intent.getLongExtra(Constants.EXTRA_COURSE_ID, -1L)
        lessonId = intent.getLongExtra(Constants.EXTRA_LESSON_ID, -1L)

        val lesson = db.getLessonById(lessonId)
        if (lesson == null) {
            Toast.makeText(this, "Lesson tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvLessonTitle.text = lesson.title

        val url = lesson.contentUrl
        if (url.isBlank()) {
            Toast.makeText(this, "URL video kosong", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.loadingOverlay.visibility = View.VISIBLE

        val mediaController = MediaController(this)
        mediaController.setAnchorView(binding.videoView)
        binding.videoView.setMediaController(mediaController)

        binding.videoView.setVideoURI(Uri.parse(url))
        binding.videoView.setOnPreparedListener { mp ->
            binding.loadingOverlay.visibility = View.GONE
            mp.start()
        }
        binding.videoView.setOnErrorListener { _, what, extra ->
            binding.loadingOverlay.visibility = View.GONE
            Toast.makeText(this, "Gagal memutar video (what=$what extra=$extra)", Toast.LENGTH_LONG).show()
            true
        }

        binding.btnFinishVideo.setOnClickListener {
            if (userId <= 0L || courseId <= 0L || lessonId <= 0L) {
                Toast.makeText(this, "Data lesson tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            db.setLessonDone(userId, courseId, lessonId, true)
            Toast.makeText(this, "Materi ditandai selesai ✅", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.videoView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.videoView.stopPlayback()
    }
}
