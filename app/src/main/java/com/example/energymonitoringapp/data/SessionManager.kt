package com.example.energymonitoringapp.data

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("energy_prefs", Context.MODE_PRIVATE)

    companion object {
        const val ROLE_ADMIN = "admin"
        const val ROLE_TEKNISI = "teknisi"
    }

    fun saveSession(token: String, role: String, name: String) {
        prefs.edit()
            .putString("token", token)
            .putString("role", role)
            .putString("name", name)
            .apply()
    }

    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun saveProfileData(name: String?, email: String?, nip: String?) {
        prefs.edit().apply {
            name?.let  { putString("name", it) }
            email?.let { putString("email", it) }
            nip?.let   { putString("nip", it) }
        }.apply()
    }

    fun getToken(): String  = prefs.getString("token", "") ?: ""
    fun getRole(): String   = prefs.getString("role", "") ?: ""
    fun getName(): String   = prefs.getString("name", "") ?: ""
    fun getEmail(): String  = prefs.getString("email", "") ?: ""
    fun getNip(): String    = prefs.getString("nip", "") ?: ""
    fun getBearerToken()    = "Bearer ${getToken()}"
    fun isLoggedIn()        = getToken().isNotBlank()
    fun isAdmin()           = getRole() == ROLE_ADMIN
    fun isTeknisi()         = getRole() == ROLE_TEKNISI

    fun clearSession() {
        val fcmToken = getFcmToken()
        prefs.edit().clear().apply()
        if (fcmToken.isNotBlank()) {
            saveFcmToken(fcmToken)
        }
    }

    /** Simpan FCM token device ke SharedPreferences */
    fun saveFcmToken(token: String) {
        prefs.edit().putString("fcm_token", token).apply()
    }

    /** Ambil FCM token device yang tersimpan */
    fun getFcmToken(): String = prefs.getString("fcm_token", "") ?: ""
}