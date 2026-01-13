package com.example.codestream.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.codestream.data.model.Certificate
import com.example.codestream.data.model.Course
import com.example.codestream.data.model.Lesson
import com.example.codestream.data.model.User

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
             CREATE TABLE users(
             id INTEGER PRIMARY KEY AUTOINCREMENT,
             name TEXT NOT NULL,
             email TEXT NOT NULL UNIQUE,
             phone TEXT DEFAULT '',
             bio TEXT DEFAULT '',
             password_hash TEXT NOT NULL
             );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE courses(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                price INTEGER NOT NULL,
                category TEXT NOT NULL
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE lessons(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                course_id INTEGER NOT NULL,
                title TEXT NOT NULL,
                type TEXT NOT NULL,
                content_url TEXT NOT NULL,
                order_index INTEGER NOT NULL,
                FOREIGN KEY(course_id) REFERENCES courses(id)
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE wishlist(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                course_id INTEGER NOT NULL,
                UNIQUE(user_id, course_id),
                FOREIGN KEY(user_id) REFERENCES users(id),
                FOREIGN KEY(course_id) REFERENCES courses(id)
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE enrollments(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                course_id INTEGER NOT NULL,
                paid_at TEXT NOT NULL,
                UNIQUE(user_id, course_id),
                FOREIGN KEY(user_id) REFERENCES users(id),
                FOREIGN KEY(course_id) REFERENCES courses(id)
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE progress(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                course_id INTEGER NOT NULL,
                lesson_id INTEGER NOT NULL,
                is_done INTEGER NOT NULL DEFAULT 0,
                UNIQUE(user_id, lesson_id),
                FOREIGN KEY(user_id) REFERENCES users(id),
                FOREIGN KEY(course_id) REFERENCES courses(id),
                FOREIGN KEY(lesson_id) REFERENCES lessons(id)
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE certificates(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                course_id INTEGER NOT NULL,
                issued_at TEXT NOT NULL,
                UNIQUE(user_id, course_id),
                FOREIGN KEY(user_id) REFERENCES users(id),
                FOREIGN KEY(course_id) REFERENCES courses(id)
            );
        """.trimIndent())

        seedCourses(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS certificates")
        db.execSQL("DROP TABLE IF EXISTS progress")
        db.execSQL("DROP TABLE IF EXISTS enrollments")
        db.execSQL("DROP TABLE IF EXISTS wishlist")
        db.execSQL("DROP TABLE IF EXISTS lessons")
        db.execSQL("DROP TABLE IF EXISTS courses")
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    private fun seedCourses(db: SQLiteDatabase) {
        val courses = listOf(
            Course(0, "Dasar Kotlin untuk Android", "Belajar Kotlin dari nol untuk membuat aplikasi Android.", 99000, "Android"),
            Course(0, "Struktur Data & Algoritma", "Materi fundamental: array, linked list, stack, queue, sort, search.", 129000, "Informatika"),
            Course(0, "SQL & Database Fundamental", "Belajar database relasional dan query SQL dasar hingga menengah.", 89000, "Database"),
            Course(0, "Java OOP untuk Pemula", "Memahami konsep OOP di Java dengan contoh nyata.", 79000, "Java")
        )

        for (c in courses) {
            val cv = ContentValues().apply {
                put("title", c.title)
                put("description", c.description)
                put("price", c.price)
                put("category", c.category)
            }
            val courseId = db.insert("courses", null, cv)

            // ====== ONLINE CONTENT (MP4 + PDF) ======
            // MP4 direct (bisa diputar pakai VideoView / ExoPlayer)
            val mp4_1 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            val mp4_2 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
            val mp4_3 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
            val mp4_4 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"

            // PDF direct link (bisa dibuka via WebView Google viewer / pdf library)
            val pdf_basic = "https://sample-files.com/downloads/documents/pdf/basic-text.pdf"

            val lessons: List<Triple<String, String, String>> = when (c.title) {
                "Dasar Kotlin untuk Android" -> listOf(
                    Triple("Pengenalan Kotlin", "video", mp4_1),
                    Triple("Praktik Kotlin", "video", mp4_2),
                    Triple("Ringkasan Kotlin (PDF)", "document", pdf_basic)
                )

                "Struktur Data & Algoritma" -> listOf(
                    Triple("Pengantar Struktur Data", "video", mp4_2),
                    Triple("Algoritma Sorting", "video", mp4_3),
                    Triple("Ringkasan Algoritma (PDF)", "document", pdf_basic)
                )

                "SQL & Database Fundamental" -> listOf(
                    Triple("Pengenalan Database", "video", mp4_3),
                    Triple("Query Dasar SQL", "video", mp4_1),
                    Triple("Cheat Sheet SQL (PDF)", "document", pdf_basic)
                )

                "Java OOP untuk Pemula" -> listOf(
                    Triple("Konsep OOP Java", "video", mp4_4),
                    Triple("Latihan Class & Object", "video", mp4_1),
                    Triple("Ringkasan OOP (PDF)", "document", pdf_basic)
                )

                else -> listOf(
                    Triple("Pengenalan", "video", mp4_1),
                    Triple("Praktik", "video", mp4_2),
                    Triple("Ringkasan Materi (PDF)", "document", pdf_basic)
                )
            }

            lessons.forEachIndexed { idx, l ->
                val lv = ContentValues().apply {
                    put("course_id", courseId)
                    put("title", l.first)
                    put("type", l.second)        // "video" atau "document"
                    put("content_url", l.third)  // URL MP4 / URL PDF
                    put("order_index", idx)
                }
                db.insert("lessons", null, lv)
            }
        }
    }


    // AUTH
    fun register(name: String, email: String, password: String): Long {
        val hash = HashUtil.sha256(password)
        if (hash.isBlank()) return -1L

        val cv = ContentValues().apply {
            put("name", name.trim())
            put("email", email.trim().lowercase())
            put("password_hash", hash)
            put("phone", "")
            put("bio", "")
        }
        return writableDatabase.insert("users", null, cv)
    }


    fun login(email: String, password: String): User? {
        val hash = HashUtil.sha256(password)
        readableDatabase.rawQuery(
            "SELECT id,name,email,phone,bio FROM users WHERE email=? AND password_hash=? LIMIT 1",
            arrayOf(email.trim().lowercase(), hash)
        ).use { cur ->
            return if (cur.moveToFirst()) {
                User(
                    id = cur.getLong(0),
                    name = cur.getString(1),
                    email = cur.getString(2),
                    phone = cur.getString(3) ?: "",
                    bio = cur.getString(4) ?: ""
                )
            } else null
        }
    }

    fun getUser(userId: Long): User? {
        readableDatabase.rawQuery(
            "SELECT id,name,email,phone,bio FROM users WHERE id=? LIMIT 1",
            arrayOf(userId.toString())
        ).use { cur ->
            return if (cur.moveToFirst()) {
                User(
                    id = cur.getLong(0),
                    name = cur.getString(1),
                    email = cur.getString(2),
                    phone = cur.getString(3) ?: "",
                    bio = cur.getString(4) ?: ""
                )
            } else null
        }
    }

    fun updateProfile(userId: Long, name: String, email: String, phone: String, bio: String): Boolean {
        val cv = ContentValues().apply {
            put("name", name.trim())
            put("email", email.trim().lowercase())
            put("phone", phone.trim())
            put("bio", bio.trim())
        }
        val rows = writableDatabase.update(
            "users",
            cv,
            "id=?",
            arrayOf(userId.toString())
        )
        return rows > 0
    }


    // COURSES
    fun getCourses(query: String? = null): List<Course> {
        val q = query?.trim().orEmpty()
        val sql: String
        val args: Array<String>
        if (q.isBlank()) {
            sql = "SELECT id,title,description,price,category FROM courses ORDER BY id DESC"
            args = emptyArray()
        } else {
            sql = "SELECT id,title,description,price,category FROM courses WHERE title LIKE ? OR category LIKE ? ORDER BY id DESC"
            val like = "%$q%"
            args = arrayOf(like, like)
        }

        val out = mutableListOf<Course>()
        readableDatabase.rawQuery(sql, args).use { cur ->
            while (cur.moveToNext()) {
                out.add(Course(cur.getLong(0), cur.getString(1), cur.getString(2), cur.getInt(3), cur.getString(4)))
            }
        }
        return out
    }

    fun getCourse(courseId: Long): Course? {
        readableDatabase.rawQuery(
            "SELECT id,title,description,price,category FROM courses WHERE id=? LIMIT 1",
            arrayOf(courseId.toString())
        ).use { cur ->
            return if (cur.moveToFirst()) Course(cur.getLong(0), cur.getString(1), cur.getString(2), cur.getInt(3), cur.getString(4)) else null
        }
    }

    fun getLessons(userId: Long, courseId: Long): List<Lesson> {
        val out = mutableListOf<Lesson>()
        val sql = """
            SELECT l.id,l.course_id,l.title,l.type,l.content_url,l.order_index,
                   COALESCE(p.is_done,0) as is_done
            FROM lessons l
            LEFT JOIN progress p ON p.lesson_id=l.id AND p.user_id=?
            WHERE l.course_id=?
            ORDER BY l.order_index ASC
        """.trimIndent()

        readableDatabase.rawQuery(sql, arrayOf(userId.toString(), courseId.toString())).use { cur ->
            while (cur.moveToNext()) {
                out.add(
                    Lesson(
                        id = cur.getLong(0),
                        courseId = cur.getLong(1),
                        title = cur.getString(2),
                        type = cur.getString(3),
                        contentUrl = cur.getString(4),
                        orderIndex = cur.getInt(5),
                        isDone = cur.getInt(6) == 1
                    )
                )
            }
        }
        return out
    }
    fun getLessonById(lessonId: Long): Lesson? {
        readableDatabase.rawQuery(
            "SELECT id,course_id,title,type,content_url,order_index FROM lessons WHERE id=? LIMIT 1",
            arrayOf(lessonId.toString())
        ).use { cur ->
            return if (cur.moveToFirst()) {
                Lesson(
                    id = cur.getLong(0),
                    courseId = cur.getLong(1),
                    title = cur.getString(2),
                    type = cur.getString(3),
                    contentUrl = cur.getString(4),
                    orderIndex = cur.getInt(5),
                    isDone = false
                )
            } else null
        }
    }

    fun setLessonDone(userId: Long, courseId: Long, lessonId: Long, done: Boolean) {
        val cv = ContentValues().apply {
            put("user_id", userId)
            put("course_id", courseId)
            put("lesson_id", lessonId)
            put("is_done", if (done) 1 else 0)
        }
        writableDatabase.insertWithOnConflict("progress", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getCourseProgressPercent(userId: Long, courseId: Long): Int {
        val total = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM lessons WHERE course_id=?",
            arrayOf(courseId.toString())
        ).use { c -> c.moveToFirst(); c.getInt(0) }

        if (total == 0) return 0

        val done = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM progress WHERE user_id=? AND course_id=? AND is_done=1",
            arrayOf(userId.toString(), courseId.toString())
        ).use { c -> c.moveToFirst(); c.getInt(0) }

        return ((done.toDouble() / total.toDouble()) * 100.0).toInt()
    }

    // WISHLIST
    fun isWishlisted(userId: Long, courseId: Long): Boolean {
        readableDatabase.rawQuery(
            "SELECT 1 FROM wishlist WHERE user_id=? AND course_id=? LIMIT 1",
            arrayOf(userId.toString(), courseId.toString())
        ).use { c -> return c.moveToFirst() }
    }

    fun toggleWishlist(userId: Long, courseId: Long): Boolean {
        return if (isWishlisted(userId, courseId)) {
            writableDatabase.delete("wishlist", "user_id=? AND course_id=?", arrayOf(userId.toString(), courseId.toString()))
            false
        } else {
            val cv = ContentValues().apply {
                put("user_id", userId)
                put("course_id", courseId)
            }
            writableDatabase.insertWithOnConflict("wishlist", null, cv, SQLiteDatabase.CONFLICT_IGNORE)
            true
        }
    }

    fun getWishlistCourses(userId: Long): List<Course> {
        val out = mutableListOf<Course>()
        val sql = """
            SELECT c.id,c.title,c.description,c.price,c.category
            FROM courses c
            INNER JOIN wishlist w ON w.course_id=c.id
            WHERE w.user_id=?
            ORDER BY c.id DESC
        """.trimIndent()
        readableDatabase.rawQuery(sql, arrayOf(userId.toString())).use { cur ->
            while (cur.moveToNext()) {
                out.add(Course(cur.getLong(0), cur.getString(1), cur.getString(2), cur.getInt(3), cur.getString(4)))
            }
        }
        return out
    }

    // ENROLLMENT
    fun isEnrolled(userId: Long, courseId: Long): Boolean {
        readableDatabase.rawQuery(
            "SELECT 1 FROM enrollments WHERE user_id=? AND course_id=? LIMIT 1",
            arrayOf(userId.toString(), courseId.toString())
        ).use { c -> return c.moveToFirst() }
    }

    fun enroll(userId: Long, courseId: Long, paidAt: String): Boolean {
        val cv = ContentValues().apply {
            put("user_id", userId)
            put("course_id", courseId)
            put("paid_at", paidAt)
        }
        val res = writableDatabase.insertWithOnConflict("enrollments", null, cv, SQLiteDatabase.CONFLICT_IGNORE)
        return res != -1L
    }

    fun getEnrolledCourses(userId: Long): List<Course> {
        val out = mutableListOf<Course>()
        val sql = """
            SELECT c.id,c.title,c.description,c.price,c.category
            FROM courses c
            INNER JOIN enrollments e ON e.course_id=c.id
            WHERE e.user_id=?
            ORDER BY e.id DESC
        """.trimIndent()
        readableDatabase.rawQuery(sql, arrayOf(userId.toString())).use { cur ->
            while (cur.moveToNext()) {
                out.add(Course(cur.getLong(0), cur.getString(1), cur.getString(2), cur.getInt(3), cur.getString(4)))
            }
        }
        return out
    }

    // CERTIFICATE
    fun ensureCertificateIfEligible(userId: Long, courseId: Long): Boolean {
        val progress = getCourseProgressPercent(userId, courseId)
        if (progress < 100) return false

        val exists = readableDatabase.rawQuery(
            "SELECT 1 FROM certificates WHERE user_id=? AND course_id=? LIMIT 1",
            arrayOf(userId.toString(), courseId.toString())
        ).use { c -> c.moveToFirst() }

        if (exists) return true

        val issuedAt = java.text.SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            java.util.Locale.getDefault()
        ).format(java.util.Date())
        val cv = ContentValues().apply {
            put("user_id", userId)
            put("course_id", courseId)
            put("issued_at", issuedAt)
        }
        val res = writableDatabase.insertWithOnConflict("certificates", null, cv, SQLiteDatabase.CONFLICT_IGNORE)
        return res != -1L
    }

    fun getCertificates(userId: Long): List<Certificate> {
        val out = mutableListOf<Certificate>()
        val db = readableDatabase

        // certificates table tidak punya course_title.
        // Ambil judul course dari table courses.
        val sql = """
            SELECT c.id, c.course_id, co.title AS course_title, c.issued_at
            FROM certificates c
            INNER JOIN courses co ON co.id = c.course_id
            WHERE c.user_id = ?
            ORDER BY c.issued_at DESC
        """.trimIndent()

        val cur = db.rawQuery(sql, arrayOf(userId.toString()))
        cur.use {
            while (it.moveToNext()) {
                out.add(
                    Certificate(
                        id = it.getLong(0),
                        courseId = it.getLong(1),
                        courseTitle = it.getString(2),
                        issuedAt = it.getString(3)
                    )
                )
            }
        }
        return out
    }


    companion object {
        private const val DB_NAME = "codestream.db"
        private const val DB_VERSION = 3
    }
}
