package com.app.smartkantin.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveSession(userId: Int, nama: String, email: String, role: String, namaToko: String? = null) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_NAMA, nama)
            putString(KEY_EMAIL, email)
            putString(KEY_ROLE, role)
            putString(KEY_NAMA_TOKO, namaToko)
            putBoolean(KEY_IS_LOGIN, true)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGIN, false)

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    fun getNama(): String? = prefs.getString(KEY_NAMA, null)

    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun getRole(): String? = prefs.getString(KEY_ROLE, null)

    fun getNamaToko(): String? = prefs.getString(KEY_NAMA_TOKO, null)

    fun logout() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME = "smartkantin_session"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_NAMA = "key_nama"
        private const val KEY_EMAIL = "key_email"
        private const val KEY_ROLE = "key_role"
        private const val KEY_NAMA_TOKO = "key_nama_toko"
        private const val KEY_IS_LOGIN = "key_is_login"
    }
}