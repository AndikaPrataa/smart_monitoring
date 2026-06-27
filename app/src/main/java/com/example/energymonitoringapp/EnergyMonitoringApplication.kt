package com.example.energymonitoringapp

import android.app.Application
import com.example.energymonitoringapp.pushNotif.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EnergyMonitoringApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)  // ← pindah ke sini
    }
}