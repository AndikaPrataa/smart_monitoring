package com.example.energymonitoringapp.pushNotif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.energymonitoringapp.MainActivity
import com.example.energymonitoringapp.R

object NotificationHelper {
    const val CHANNEL_ID_ALERT      = "energy_alert_channel"
    const val CHANNEL_ID_ASSIGNMENT = "energy_assignment_channel"
    const val CHANNEL_ID_COMPLETED  = "energy_completed_channel"

    fun createNotificationChannels(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val manager = context.getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID_ALERT,
                    "Peringatan Sensor",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifikasi peringatan sensor energi dan lingkungan"
                    enableVibration(true)
                }
            )

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID_ASSIGNMENT,
                    "Penugasan Teknisi",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifikasi penugasan tugas untuk teknisi"
                    enableVibration(true)
                }
            )

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID_COMPLETED,
                    "Tugas Selesai",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notifikasi konfirmasi tugas yang telah diselesaikan teknisi"
                }
            )
        }
    }

    fun showNotification(
        context   : Context,
        title     : String,
        body      : String,
        notifId   : Int    = 0,
        channelId : String = CHANNEL_ID_ALERT,
        extraData : Map<String, String> = emptyMap()
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notif_id", notifId)
            extraData.forEach { (key, value) -> putExtra(key, value) }
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notifId.takeIf { it != 0 } ?: System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_warning_24)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(
            notifId.takeIf { it != 0 } ?: System.currentTimeMillis().toInt(),
            notification
        )
    }
}
