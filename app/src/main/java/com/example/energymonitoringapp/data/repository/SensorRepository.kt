package com.example.energymonitoringapp.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.energymonitoringapp.data.api.ApiService
import com.example.energymonitoringapp.data.local.NotifEntity
import com.example.energymonitoringapp.data.local.SensorDao
import com.example.energymonitoringapp.data.local.SensorDayaEntity
import com.example.energymonitoringapp.data.local.SensorLingkunganEntity
import com.example.energymonitoringapp.data.local.SensorListrikEntity
import com.example.energymonitoringapp.data.response.DayaListrikResponse
import com.example.energymonitoringapp.data.response.KonsumsiEnergiResponse
import com.example.energymonitoringapp.data.response.LingkunganResponse
import com.example.energymonitoringapp.data.response.ListrikResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensorRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: ApiService,
    private val dao: SensorDao
) {
    fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    suspend fun fetchLingkungan(): LingkunganResponse? {
        return if (isOnline()) {
            runCatching { api.getMonitoringAll() }
                .getOrNull()
                ?.takeIf { it.isSuccessful }
                ?.body()
                ?.also { response ->
                    dao.saveLingkungan(
                        SensorLingkunganEntity(
                            suhu       = response.data?.suhu,
                            kelembapan = response.data?.kelembapan,
                            pm25       = response.data?.pm25,
                            pm10       = response.data?.pm10,
                            gasCo      = response.data?.gasCo,
                            gasCo2     = response.data?.gasCo2,
                            tvoc       = response.data?.tvoc,
                            cahaya     = response.data?.cahaya,
                            kebisingan = response.data?.kebisingan,
                            lokasi     = response.data?.lokasi,
                            waktu      = response.data?.waktu,
                            ieqScore   = response.ieq?.score?.toInt(),
                            ieqStatus  = response.ieq?.status
                        )
                    )
                }
        } else null
    }
    fun getLocalLingkungan(): Flow<SensorLingkunganEntity?> =
        dao.getLatestLingkungan()
    suspend fun fetchListrik(): ListrikResponse? {
        return if (isOnline()) {
            runCatching { api.getListrikAll() }
                .getOrNull()
                ?.takeIf { it.isSuccessful }
                ?.body()
                ?.also { response ->
                    dao.saveListrik(
                        SensorListrikEntity(
                            totalVoltage  = response.total?.totalVoltage,
                            totalCurrent  = response.total?.totalCurrent,
                            timeStamp     = response.timestamp
                        )
                    )
                }
        } else null
    }
    fun getLocalListrik(): Flow<SensorListrikEntity?> =
        dao.getLatestListrik()
    suspend fun fetchDaya(): DayaListrikResponse? {
        return if (isOnline()) {
            runCatching { api.getDayaAll() }
                .getOrNull()
                ?.takeIf { it.isSuccessful }
                ?.body()
                ?.also { response ->
                    dao.saveDaya(
                        SensorDayaEntity(
                            totalActivePower = response.totalActivePower?.value,
                            unit             = response.totalActivePower?.unit,
                            energyKwh        = response.biayaRealtime?.energyKwh,
                            biaya            = response.biayaRealtime?.biaya,
                            timeStamp        = null
                        )
                    )
                }
        } else null
    }
    fun getLocalDaya(): Flow<SensorDayaEntity?> =
        dao.getLatestDaya()

    suspend fun fetchIke(): KonsumsiEnergiResponse? {
        return if (isOnline()) {
            runCatching { api.getKonsumsiEnergi() }
                .getOrNull()
                ?.takeIf { it.isSuccessful }
                ?.body()
        } else null
    }
    suspend fun fetchNotif(): List<NotifEntity> {
        return if (isOnline()) {
            runCatching { api.getAllNotifications() }
                .getOrNull()
                ?.takeIf { it.isSuccessful }
                ?.body()
                ?.map { item ->
                    NotifEntity(
                        id        = item.id ?: 0,
                        role      = "all",
                        kategori  = item.kategori,
                        sensor    = item.sensor,
                        title     = item.title,
                        level     = item.level,
                        value     = item.value,
                        unit      = item.unit,
                        message   = item.message,
                        lokasi    = item.lokasi,
                        status    = item.status,
                        timestamp = item.timestamp
                    )
                }
                ?.also { dao.saveAllNotif(it) }
                ?: dao.getNotifByRoleOnce("all")
        } else {
            dao.getNotifByRoleOnce("all")
        }
    }
    fun getLocalNotif(): Flow<List<NotifEntity>> =
        dao.getNotifByRole("all")
    suspend fun cleanOldData() {
        val threshold = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        dao.deleteOldLingkungan(threshold)
        dao.deleteOldListrik(threshold)
        dao.deleteOldDaya(threshold)
    }
}