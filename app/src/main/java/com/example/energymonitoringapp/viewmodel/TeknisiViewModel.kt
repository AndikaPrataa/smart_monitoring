package com.example.energymonitoringapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.energymonitoringapp.data.repository.NotifRepository
import com.example.energymonitoringapp.data.response.NotifTeknisiItem
import com.example.energymonitoringapp.data.response.NotifTeknisiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class TeknisiViewModel @Inject constructor(
    private val repository: NotifRepository
) : ViewModel() {
    private val _notifications = MutableStateFlow<NotifTeknisiResponse?>(null)
    val notifications: StateFlow<NotifTeknisiResponse?> = _notifications
    private val _notifDetail = MutableStateFlow<NotifTeknisiItem?>(null)
    val notifDetail: StateFlow<NotifTeknisiItem?> = _notifDetail
    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting
    private val _konfirmasiSuccess = MutableStateFlow(false)
    val konfirmasiSuccess: StateFlow<Boolean> = _konfirmasiSuccess
    private val _konfirmasiError = MutableStateFlow<String?>(null)
    val konfirmasiError: StateFlow<String?> = _konfirmasiError
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchNotifications()
        observeLocalData()
    }

    fun fetchNotifications() = viewModelScope.launch {
        _isLoading.value = true
        repository.fetchTeknisiNotif()
            .onSuccess { if (it.isSuccessful) _notifications.value = it.body() }
            .onFailure { _errorMessage.value = it.message }
        _isLoading.value = false
    }

    fun fetchNotifDetail(id: Int) = viewModelScope.launch {
        _isLoading.value = true
        repository.fetchTeknisiNotifDetail(id)
            .onSuccess {
                if (it.isSuccessful) {
                    _notifDetail.value = it.body()?.data
                } else {
                    loadNotifDetailFromLocal(id)
                }
            }
            .onFailure {
                _errorMessage.value = it.message
                loadNotifDetailFromLocal(id)
            }
        _isLoading.value = false
    }

    fun konfirmasiPenanganan(
        notifId       : Int,
        fotoUri       : Uri?,
        actionTaken   : String,
        additionalNote: String
    ) = viewModelScope.launch {
        _isSubmitting.value    = true
        _konfirmasiError.value = null
        repository.konfirmasiPenanganan(notifId, fotoUri, actionTaken, additionalNote)
            .onSuccess { response ->
                if (response.isSuccessful) {
                    _konfirmasiSuccess.value = true
                    fetchNotifications()
                } else {
                    _konfirmasiError.value = when (response.code()) {
                        422  -> "Tugas ini sudah diselesaikan sebelumnya"
                        404  -> "Tugas tidak ditemukan"
                        else -> "Gagal mengirim konfirmasi"
                    }
                }
            }
            .onFailure { _konfirmasiError.value = it.message ?: "Terjadi kesalahan jaringan" }
        _isSubmitting.value = false
    }

    fun resetKonfirmasiState() {
        _konfirmasiSuccess.value = false
        _konfirmasiError.value   = null
    }

    private suspend fun loadNotifDetailFromLocal(id: Int) {
        repository.getLocalNotifTeknisiById(id)?.let { entity ->
            _notifDetail.value = NotifTeknisiItem(
                id             = entity.id,
                kategori       = entity.kategori,
                sensor         = entity.sensor,
                title          = entity.title,
                level          = entity.level,
                value          = entity.value,
                unit           = entity.unit,
                message        = entity.message,
                lokasi         = entity.lokasi,
                status         = entity.status,
                assignedTo     = entity.assignedTo,
                assignedAt     = entity.assignedAt,
                completedAt    = entity.completedAt,
                fieldPhoto     = entity.fieldPhoto,
                actionTaken    = entity.actionTaken,
                additionalNote = entity.additionalNote,
                fieldPhotoUrl  = entity.fieldPhotoUrl,
                timestamp      = entity.timestamp
            )
        }
    }

    private fun observeLocalData() {
        viewModelScope.launch {
            repository.getLocalNotifTeknisi().collect { localList ->
                if (_notifications.value == null && localList.isNotEmpty()) {
                    _notifications.value = NotifTeknisiResponse(
                        success = true,
                        message = "Data lokal",
                        total   = localList.size,
                        data    = localList.map { entity ->
                            NotifTeknisiItem(
                                id             = entity.id,
                                kategori       = entity.kategori,
                                sensor         = entity.sensor,
                                title          = entity.title,
                                level          = entity.level,
                                value          = entity.value,
                                unit           = entity.unit,
                                message        = entity.message,
                                lokasi         = entity.lokasi,
                                status         = entity.status,
                                assignedTo     = entity.assignedTo,
                                assignedAt     = entity.assignedAt,
                                completedAt    = entity.completedAt,
                                fieldPhoto     = entity.fieldPhoto,
                                actionTaken    = entity.actionTaken,
                                additionalNote = entity.additionalNote,
                                fieldPhotoUrl  = entity.fieldPhotoUrl,
                                timestamp      = entity.timestamp
                            )
                        }
                    )
                }
            }
        }
    }

    fun clearError() { _errorMessage.value = null }
}