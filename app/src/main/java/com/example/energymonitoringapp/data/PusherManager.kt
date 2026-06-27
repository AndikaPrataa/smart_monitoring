package com.example.energymonitoringapp.data

import android.util.Log
import com.example.energymonitoringapp.data.response.AllNotifResponseItem
import com.example.energymonitoringapp.data.response.BiayaRealtime
import com.example.energymonitoringapp.data.response.DayaListrikResponse
import com.example.energymonitoringapp.data.response.LingkunganData
import com.example.energymonitoringapp.data.response.LingkunganResponse
import com.example.energymonitoringapp.data.response.ListrikResponse
import com.google.gson.Gson
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange

object PusherManager {

    private const val TAG = "PusherManager"
    private val gson = Gson()
    private var pusher: Pusher? = null

    var onLingkunganUpdated: ((LingkunganData) -> Unit)? = null
    var onMonitoringHistoryUpdated: ((LingkunganResponse) -> Unit)? = null

    var onBiayaUpdated: ((BiayaRealtime) -> Unit)? = null
    var onDayaUpdated: ((DayaListrikResponse) -> Unit)? = null
    var onListrikUpdated: ((ListrikResponse) -> Unit)? = null

    var onNewNotification: ((AllNotifResponseItem) -> Unit)? = null

    fun connect() {
        if (pusher != null) {
            Log.d(TAG, "Pusher sudah terhubung, skip connect")
            return
        }

        val options = PusherOptions().apply {
            setHost("192.168.1.6")
            setWsPort(8080)
            setWssPort(8080)
            isUseTLS =  false
        }

        pusher = Pusher("efksfxfxxfrzuetobo5f", options)

        pusher?.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                Log.d(TAG, "Pusher: ${change.previousState} → ${change.currentState}")
            }
            override fun onError(message: String, code: String?, e: Exception?) {
                Log.e(TAG, "Pusher error: $message | code: $code")
            }
        }, ConnectionState.ALL)

        subscribeAll()
    }

    private fun subscribeAll() {
        subscribeLingkungan()
        subscribeMonitoringHistory()
        subscribeBiaya()
        subscribeDaya()
        subscribeListrik()
        subscribeNotifications()
    }

    private fun subscribeLingkungan() {
        pusher?.subscribe("monitoring.suhu")
            ?.bind("suhu.updated") { event ->
                Log.d(TAG, "[monitoring.suhu] ${event.data}")
                runCatching {
                    gson.fromJson(event.data, LingkunganData::class.java)
                }.onSuccess {
                    onLingkunganUpdated?.invoke(it)
                }.onFailure {
                    Log.e(TAG, "Parse LingkunganData error: ${it.message}")
                }
            }
    }

    private fun subscribeMonitoringHistory() {
        pusher?.subscribe("monitoring.history")
            ?.bind("monitoring.history.updated") { event ->
                Log.d(TAG, "[monitoring.history] ${event.data}")
                runCatching {
                    gson.fromJson(event.data, LingkunganResponse::class.java)
                }.onSuccess {
                    onMonitoringHistoryUpdated?.invoke(it)
                }.onFailure {
                    Log.e(TAG, "Parse MonitoringHistory error: ${it.message}")
                }
            }
    }

    private fun subscribeBiaya() {
        pusher?.subscribe("daya.biaya")
            ?.bind("daya.biaya.updated") { event ->
                Log.d(TAG, "[daya.biaya] ${event.data}")
                runCatching {
                    gson.fromJson(event.data, BiayaRealtime::class.java)
                }.onSuccess {
                    onBiayaUpdated?.invoke(it)
                }.onFailure {
                    Log.e(TAG, "Parse BiayaRealtime error: ${it.message}")
                }
            }
    }

    private fun subscribeDaya() {
        pusher?.subscribe("daya.listrik")
            ?.bind("daya.updated") { event ->
                Log.d(TAG, "[daya.listrik] ${event.data}")
                runCatching {
                    gson.fromJson(event.data, DayaListrikResponse::class.java)
                }.onSuccess {
                    onDayaUpdated?.invoke(it)
                }.onFailure {
                    Log.e(TAG, "Parse DayaListrik error: ${it.message}")
                }
            }
    }

    private fun subscribeListrik() {
        pusher?.subscribe("listrik.all")
            ?.bind("listrik.updated") { event ->
                Log.d(TAG, "[listrik.all] ${event.data}")
                runCatching {
                    gson.fromJson(event.data, ListrikResponse::class.java)
                }.onSuccess {
                    onListrikUpdated?.invoke(it)
                }.onFailure {
                    Log.e(TAG, "Parse ListrikResponse error: ${it.message}")
                }
            }
    }

    private fun subscribeNotifications() {
        pusher?.subscribe("notifications")
            ?.bind("notification.created") { event ->
                Log.d(TAG, "[notifications] ${event.data}")
                runCatching {
                    gson.fromJson(event.data, AllNotifResponseItem::class.java)
                }.onSuccess {
                    onNewNotification?.invoke(it)
                }.onFailure {
                    Log.e(TAG, "Parse Notification error: ${it.message}")
                }
            }
    }

    fun disconnect() {
        pusher?.disconnect()
        pusher = null
        Log.d(TAG, "Pusher disconnected")
    }
}