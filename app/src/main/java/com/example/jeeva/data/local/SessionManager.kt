package com.example.jeeva.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("jeeva_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_PHONE_NUMBER = "user_phone"
    }

    fun setLogin(isLoggedIn: Boolean, phone: String? = null) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            putString(KEY_PHONE_NUMBER, phone)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    fun getUserPhone(): String? = prefs.getString(KEY_PHONE_NUMBER, null)

    fun logout() {
        prefs.edit().clear().apply()
    }
}
