package com.example.codestream.ui.certificate

import android.content.Context

object UserSession {
    private const val PREF = "codestream_prefs"
    private const val KEY_NAME = "user_name"

    fun getUserName(context: Context): String {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return sp.getString(KEY_NAME, "User") ?: "User"
    }

    // panggil ini waktu login sukses:
    fun saveUserName(context: Context, name: String) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        sp.edit().putString(KEY_NAME, name).apply()
    }
}
