package com.example.codestream.data.db

import android.content.Context

/**
 * Session sederhana pakai SharedPreferences.
 * Key harus konsisten supaya tidak bikin userId = -1 walau sudah login.
 */
class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setLoggedIn(userId: Long, email: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    /** Prefer pakai ini dibanding getUserId() lama */
    fun userId(): Long = prefs.getLong(KEY_USER_ID, -1L)

    fun email(): String = prefs.getString(KEY_EMAIL, "") ?: ""

    // Backward compatibility (kalau ada code lama yang manggil getUserId)
    fun getUserId(): Long = userId()

    fun logout() {
        prefs.edit().clear().apply()
    }
    fun setCourseCompleted(courseId: Long, completed: Boolean) {
        prefs.edit().putBoolean("course_completed_$courseId", completed).apply()
    }

    fun isCourseCompleted(courseId: Long): Boolean {
        return prefs.getBoolean("course_completed_$courseId", false)
    }
    private companion object {
        const val PREF_NAME = "codestream_session"
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        const val KEY_USER_ID = "userId"
        const val KEY_EMAIL = "email"
    }
}
