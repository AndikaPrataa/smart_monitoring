package com.example.energymonitoringapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.energymonitoringapp.data.SessionManager
import com.example.energymonitoringapp.data.api.ApiService
import com.example.energymonitoringapp.di.NetworkModule
import com.example.energymonitoringapp.pushNotif.FcmTokenRequest
import com.example.energymonitoringapp.pushNotif.NotificationHelper
import com.example.energymonitoringapp.view.navigation.AppNavigation
import com.example.energymonitoringapp.view.theme.EnergyMonitoringAppTheme
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var apiService: ApiService
    companion object {
        private const val TAG                    = "MainActivity"
        private const val REQUEST_NOTIFICATION   = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initializeApiToken()
        requestNotificationPermission()
        initializeFcmToken()
        setContent {
            EnergyMonitoringAppTheme {
                AppNavigation()
            }
        }
    }

    private fun initializeApiToken() {}

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!isGranted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION
                )
            }
        }
    }

    private fun initializeFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Gagal ambil FCM token", task.exception)
                return@addOnCompleteListener
            }
            val fcmToken = task.result
            Log.d(TAG, "FCM Token: $fcmToken")

            // Selalu simpan token terbaru ke lokal
            sessionManager.saveFcmToken(fcmToken)

            // Kirim ke server hanya jika sudah login
            // (jika belum login, AuthRepository.login() akan mengirimnya setelah login)
            if (sessionManager.isLoggedIn()) {
                CoroutineScope(Dispatchers.IO).launch {
                    runCatching {
                        // Pastikan token auth sudah ada sebelum kirim
                        val authToken = sessionManager.getToken()
                        if (authToken.isNotBlank()) {
                            apiService.registerFcmToken(
                                request = FcmTokenRequest(fcmToken = fcmToken)
                            )
                        }
                    }.onSuccess {
                        Log.d(TAG, "FCM token berhasil dikirim ke server")
                    }.onFailure {
                        Log.e(TAG, "Gagal kirim FCM token ke server: ${it.message}")
                    }
                }
            }
        }
    }
}
