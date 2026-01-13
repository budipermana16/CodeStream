package com.example.codestream.ui.certificate

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.codestream.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object CertificateUtils {

    private fun renderCertificateBitmap(
        context: Context,
        userName: String,
        courseTitle: String,
        issuedAt: String
    ): Bitmap {
        val w = 1200
        val h = 1700

        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.WHITE)

        val purple = Color.parseColor("#6A1B9A")

        // Border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 12f
            color = purple
        }
        val rect = RectF(60f, 60f, (w - 60).toFloat(), (h - 60).toFloat())
        canvas.drawRoundRect(rect, 40f, 40f, borderPaint)

        // Logo
        val logoBmp = BitmapFactory.decodeResource(context.resources, R.drawable.codestream_logo)
        val logoSize = 140
        val logoScaled = Bitmap.createScaledBitmap(logoBmp, logoSize, logoSize, true)
        canvas.drawBitmap(logoScaled, (w / 2f) - (logoSize / 2f), 130f, null)

        // Title
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textSize = 44f
        }
        canvas.drawText("CERTIFICATE OF COMPLETION", w / 2f, 360f, titlePaint)

        val smallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            textSize = 22f
        }
        canvas.drawText("This certificate is proudly presented to", w / 2f, 420f, smallPaint)

        // User
        val userPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = purple
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textSize = 56f
        }
        canvas.drawText(userName.ifBlank { "User" }, w / 2f, 520f, userPaint)

        canvas.drawText("for successfully completing the course", w / 2f, 585f, smallPaint)

        // Course (wrap)
        val coursePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textSize = 34f
        }
        drawCenteredMultiline(canvas, courseTitle.ifBlank { "-" }, w / 2f, 650f, coursePaint, maxWidth = w - 220)

        // Issued
        val issuedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GRAY
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            textSize = 22f
        }
        canvas.drawText("Issued: ${issuedAt.ifBlank { "-" }}", w / 2f, 820f, issuedPaint)

        // Footer
        canvas.drawText("CodeStream Academy", w / 2f, (h - 150).toFloat(), issuedPaint)

        return bmp
    }

    private fun drawCenteredMultiline(
        canvas: Canvas,
        text: String,
        centerX: Float,
        startY: Float,
        paint: Paint,
        maxWidth: Int
    ) {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var current = ""

        for (w in words) {
            val test = if (current.isEmpty()) w else "$current $w"
            if (paint.measureText(test) <= maxWidth) {
                current = test
            } else {
                if (current.isNotEmpty()) lines.add(current)
                current = w
            }
        }
        if (current.isNotEmpty()) lines.add(current)

        var y = startY
        val lineHeight = (paint.fontMetrics.bottom - paint.fontMetrics.top) + 8f
        for (line in lines.take(3)) {
            canvas.drawText(line, centerX, y, paint)
            y += lineHeight
        }
    }

    fun saveCertificateToGallery(
        context: Context,
        userName: String,
        courseTitle: String,
        issuedAt: String
    ): Boolean {
        return try {
            val bitmap = renderCertificateBitmap(context, userName, courseTitle, issuedAt)

            val filename = "codestream_certificate_${System.currentTimeMillis()}.png"
            val resolver = context.contentResolver

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CodeStream")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: return false

            resolver.openOutputStream(uri)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            } ?: return false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }

            true
        } catch (_: Exception) {
            false
        }
    }

    fun createTempCertificateFile(
        context: Context,
        userName: String,
        courseTitle: String,
        issuedAt: String
    ): Uri? {
        return try {
            val bitmap = renderCertificateBitmap(context, userName, courseTitle, issuedAt)

            val cacheDir = File(context.cacheDir, "certificates")
            if (!cacheDir.exists()) cacheDir.mkdirs()

            val file = File(cacheDir, "certificate_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (_: Exception) {
            null
        }
    }

    fun shareImage(context: Context, uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Certificate"))
    }
}
