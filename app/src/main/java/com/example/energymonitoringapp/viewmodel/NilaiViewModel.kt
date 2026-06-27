package com.example.energymonitoringapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.energymonitoringapp.data.PusherManager
import com.example.energymonitoringapp.data.SessionManager
import com.example.energymonitoringapp.data.repository.SensorRepository
import com.example.energymonitoringapp.data.response.AllNotifResponseItem
import com.example.energymonitoringapp.data.response.BiayaRealtime
import com.example.energymonitoringapp.data.response.DayaListrikResponse
import com.example.energymonitoringapp.data.response.Ieq
import com.example.energymonitoringapp.data.response.KonsumsiEnergiResponse
import com.example.energymonitoringapp.data.response.LingkunganData
import com.example.energymonitoringapp.data.response.LingkunganResponse
import com.example.energymonitoringapp.data.response.ListrikResponse
import com.example.energymonitoringapp.data.response.ListrikTotal
import com.example.energymonitoringapp.data.response.TotalActivePower
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NilaiViewModel @Inject constructor(
    private val repository: SensorRepository,
    private val session: SessionManager
) : ViewModel() {
    private val _lingkungan = MutableStateFlow<LingkunganResponse?>(null)
    val lingkungan: StateFlow<LingkunganResponse?> = _lingkungan
    private val _daya = MutableStateFlow<DayaListrikResponse?>(null)
    val daya: StateFlow<DayaListrikResponse?> = _daya
    private val _listrik = MutableStateFlow<ListrikResponse?>(null)
    val listrik: StateFlow<ListrikResponse?> = _listrik
    private val _ike = MutableStateFlow<KonsumsiEnergiResponse?>(null)
    val ike: StateFlow<KonsumsiEnergiResponse?> = _ike
    private val _allNotif = MutableStateFlow<List<AllNotifResponseItem>>(emptyList())
    val allNotif: StateFlow<List<AllNotifResponseItem>> = _allNotif
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    companion object {
        private const val AUTO_REFRESH_INTERVAL_MS = 15_000L // 60 detik
    }

    private var autoRefreshJob: Job? = null
    val activeNotifCount: Int get() = _allNotif.value.count { it.status == "aktif" }
    val bahayaCount: Int      get() = _allNotif.value.count {
        it.level == "bahaya" && it.status == "aktif"
    }
    init {
        fetchAll()
        observeLocalData()
        setupWebSocket()
        startAutoRefresh()
    }
    fun fetchAll() = viewModelScope.launch {
        _isLoading.value = true
        _isOffline.value = !repository.isOnline()
        if (repository.isOnline()) {
            repository.fetchLingkungan()?.let { _lingkungan.value = it }
            repository.fetchDaya()?.let { _daya.value = it }
            repository.fetchListrik()?.let { _listrik.value = it }
            repository.fetchIke()?.let { _ike.value = it }
            val notifList = repository.fetchNotif()
            _allNotif.value = notifList.map { entity ->
                AllNotifResponseItem(
                    id        = entity.id,
                    kategori  = entity.kategori,
                    sensor    = entity.sensor,
                    title     = entity.title,
                    level     = entity.level,
                    value     = entity.value,
                    unit      = entity.unit,
                    message   = entity.message,
                    lokasi    = entity.lokasi,
                    status    = entity.status,
                    timestamp = entity.timestamp
                )
            }
            repository.cleanOldData()
        }
        _isLoading.value = false
    }
    private fun observeLocalData() {
        viewModelScope.launch {
            repository.getLocalLingkungan().collect { local ->
                if (!repository.isOnline() && local != null) {
                    _lingkungan.value = LingkunganResponse(
                        data = LingkunganData(
                            suhu       = local.suhu,
                            kelembapan = local.kelembapan,
                            pm25       = local.pm25,
                            pm10       = local.pm10,
                            gasCo      = local.gasCo,
                            gasCo2     = local.gasCo2,
                            tvoc       = local.tvoc,
                            cahaya     = local.cahaya,
                            kebisingan = local.kebisingan,
                            lokasi     = local.lokasi,
                            waktu      = local.waktu
                        ),
                        ieq = Ieq(
                            score  = local.ieqScore?.toDouble(),
                            status = local.ieqStatus
                        )
                    )
                }
            }
        }
        viewModelScope.launch {
            repository.getLocalDaya().collect { local ->
                if (!repository.isOnline() && local != null) {
                    _daya.value = DayaListrikResponse(
                        totalActivePower = TotalActivePower(
                            value = local.totalActivePower,
                            unit  = local.unit
                        ),
                        biayaRealtime = BiayaRealtime(
                            energyKwh = local.energyKwh,
                            biaya     = local.biaya
                        )
                    )
                }
            }
        }
        viewModelScope.launch {
            repository.getLocalListrik().collect { local ->
                if (!repository.isOnline() && local != null) {
                    _listrik.value = ListrikResponse(
                        total = ListrikTotal(
                            totalVoltage  = local.totalVoltage,
                            totalCurrent  = local.totalCurrent
                        ),
                        timestamp = local.timeStamp
                    )
                }
            }
        }
        viewModelScope.launch {
            repository.getLocalNotif().collect { localList ->
                if (_allNotif.value.isEmpty() && localList.isNotEmpty()) {
                    _allNotif.value = localList.map { entity ->
                        AllNotifResponseItem(
                            id        = entity.id,
                            kategori  = entity.kategori,
                            sensor    = entity.sensor,
                            title     = entity.title,
                            level     = entity.level,
                            value     = entity.value,
                            unit      = entity.unit,
                            message   = entity.message,
                            lokasi    = entity.lokasi,
                            status    = entity.status,
                            timestamp = entity.timestamp
                        )
                    }
                }
            }
        }
    }
    private fun setupWebSocket() {
        PusherManager.onLingkunganUpdated = { newData ->
            _lingkungan.value = _lingkungan.value?.copy(data = newData) }
        PusherManager.onMonitoringHistoryUpdated = { fullResponse ->
            _lingkungan.value = fullResponse }
        PusherManager.onBiayaUpdated = { newBiaya ->
            _daya.value = _daya.value?.copy(biayaRealtime = newBiaya) }
        PusherManager.onDayaUpdated = { newDaya ->
            _daya.value = newDaya }
        PusherManager.onListrikUpdated = { newListrik ->
            _listrik.value = newListrik }
        PusherManager.onNewNotification = { newNotif ->
            _allNotif.value = listOf(newNotif) + _allNotif.value }
        if (session.isLoggedIn()) {
            PusherManager.connect()
        }
    }

    fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(AUTO_REFRESH_INTERVAL_MS)
                if (repository.isOnline()) fetchAll()
            }
        }
    }

    fun stopAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

    fun clearError() { _errorMessage.value = null }

    override fun onCleared() {
        super.onCleared()
        stopAutoRefresh()
        PusherManager.disconnect()
    }
}