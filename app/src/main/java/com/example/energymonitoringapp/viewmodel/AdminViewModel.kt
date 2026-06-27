package com.example.energymonitoringapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.energymonitoringapp.data.repository.NotifRepository
import com.example.energymonitoringapp.data.response.ListTeknisiResponse
import com.example.energymonitoringapp.data.response.NotifAdminItem
import com.example.energymonitoringapp.data.response.TeknisiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: NotifRepository
) : ViewModel() {

    private val _notifDetail = MutableStateFlow<NotifAdminItem?>(null)
    val notifDetail: StateFlow<NotifAdminItem?> = _notifDetail
    private val _teknisiList = MutableStateFlow<ListTeknisiResponse?>(null)
    val teknisiList: StateFlow<ListTeknisiResponse?> = _teknisiList
    private val _isAssigning = MutableStateFlow(false)
    val isAssigning: StateFlow<Boolean> = _isAssigning
    private val _assignSuccess = MutableStateFlow(false)
    val assignSuccess: StateFlow<Boolean> = _assignSuccess
    private val _assignError = MutableStateFlow<String?>(null)
    val assignError: StateFlow<String?> = _assignError
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _errorMessage = MutableStateFlow<String?>(null)

    val pagingAktif: Flow<PagingData<NotifAdminItem>> =
        repository.getPagingAdminNotif(status = "aktif").cachedIn(viewModelScope)

    val pagingProses: Flow<PagingData<NotifAdminItem>> =
        repository.getPagingAdminNotif(status = "proses").cachedIn(viewModelScope)

    val pagingSelesai: Flow<PagingData<NotifAdminItem>> =
        repository.getPagingAdminNotif(status = "selesai").cachedIn(viewModelScope)

    init {
        fetchListTeknisi()
        observeLocalTeknisi()
    }

    fun fetchListTeknisi() = viewModelScope.launch {
        repository.fetchListTeknisi()
            .onSuccess { if (it.isSuccessful) _teknisiList.value = it.body() }
            .onFailure { _errorMessage.value = it.message }
    }

    private fun observeLocalTeknisi() {
        viewModelScope.launch {
            repository.getLocalTeknisi().collect { localList ->
                if (_teknisiList.value == null && localList.isNotEmpty()) {
                    _teknisiList.value = ListTeknisiResponse(
                        success = true,
                        message = "Data lokal",
                        total   = localList.size,
                        data    = localList.map { entity ->
                            TeknisiItem(
                                id              = entity.id,
                                name            = entity.name,
                                email           = entity.email,
                                nip             = entity.nip,
                                role            = entity.role,
                                statusTeknisi   = entity.statusTeknisi,
                                canReceiveTask  = entity.canReceiveTask,
                                activeTaskCount = entity.activeTaskCount
                            )
                        }
                    )
                }
            }
        }
    }

    fun fetchNotifDetail(id: Int) = viewModelScope.launch {
        _isLoading.value = true
        repository.fetchAdminNotifDetail(id)
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

    private suspend fun loadNotifDetailFromLocal(id: Int) {
        repository.getLocalNotifAdminById(id)?.let { entity ->
            _notifDetail.value = NotifAdminItem(
                id         = entity.id,
                kategori   = entity.kategori,
                sensor     = entity.sensor,
                title      = entity.title,
                level      = entity.level,
                value      = entity.value,
                unit       = entity.unit,
                message    = entity.message,
                lokasi     = entity.lokasi,
                status     = entity.status,
                assignedTo = entity.assignedTo,
                assignedAt = entity.assignedAt,
                timestamp  = entity.timestamp
            )
        }
    }

    fun assignTeknisi(notifId: Int, teknisiId: Int) = viewModelScope.launch {
        _isAssigning.value = true
        _assignError.value  = null
        repository.assignTeknisi(notifId, teknisiId)
            .onSuccess { response ->
                if (response.isSuccessful) {
                    _assignSuccess.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    _assignError.value = when (response.code()) {
                        422 -> "Teknisi sedang memiliki tugas aktif atau bukan teknisi"
                        404 -> "Notifikasi tidak ditemukan"
                        else -> errorBody ?: "Gagal menugaskan teknisi"
                    }
                }
            }
            .onFailure { _assignError.value = it.message ?: "Terjadi kesalahan jaringan" }
        _isAssigning.value = false
    }

    fun resetAssignState() {
        _assignSuccess.value = false
        _assignError.value   = null
    }

    fun clearError() { _errorMessage.value = null }
}