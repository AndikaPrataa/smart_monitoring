package com.example.energymonitoringapp.pushNotif

import android.util.Log
import com.example.energymonitoringapp.data.SessionManager
import com.example.energymonitoringapp.data.api.ApiService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EnergyFCMService : FirebaseMessagingService() {
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var apiService: ApiService
    companion object {
        private const val TAG = "EnergyFCMService"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Token baru: $token")
        sessionManager.saveFcmToken(token)
        if (sessionManager.isLoggedIn()) {
            sendTokenToServer(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Pesan dari: ${remoteMessage.from}")
        Log.d(TAG, "Data payload: ${remoteMessage.data}")
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "EcoUNILA"
        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "Ada notifikasi baru"
        val notifId = remoteMessage.data["notif_id"]?.toIntOrNull() ?: 0
        val type    = remoteMessage.data["type"] ?: "alert"
        val channelId = when (type) {
            "assignment"     -> NotificationHelper.CHANNEL_ID_ASSIGNMENT
            "task_completed" -> NotificationHelper.CHANNEL_ID_COMPLETED
            else             -> NotificationHelper.CHANNEL_ID_ALERT
        }
        NotificationHelper.showNotification(
            context   = this,
            title     = title,
            body      = body,
            notifId   = notifId,
            channelId = channelId,
            extraData = remoteMessage.data
        )
    }

    private fun sendTokenToServer(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                apiService.registerFcmToken(
                    request = FcmTokenRequest(fcmToken = token)
                )
            }.onSuccess {
                Log.d(TAG, "FCM token berhasil dikirim ke server")
            }.onFailure {
                Log.e(TAG, "Gagal kirim FCM token ke server: ${it.message}")
            }
        }
    }
}
