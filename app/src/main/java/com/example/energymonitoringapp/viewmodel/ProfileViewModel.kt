package com.example.energymonitoringapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.energymonitoringapp.data.repository.UserRepository
import com.example.energymonitoringapp.data.response.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    private val _user = MutableStateFlow<UserData?>(null)
    val user: StateFlow<UserData?> = _user
    private val _updateSuccess = MutableStateFlow<String?>(null)
    val updateSuccess: StateFlow<String?> = _updateSuccess
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    init { loadProfile() }
    fun loadProfile() = viewModelScope.launch {
        _isLoading.value = true
        repository.getProfile()
            .onSuccess {
                if (it.isSuccessful) {
                    _user.value = it.body()?.data
                } else {
                    loadLocalProfile()
                }
            }
            .onFailure {
                loadLocalProfile()
            }
        _isLoading.value = false
    }
    private suspend fun loadLocalProfile() {
        repository.getLocalProfile()?.let { localData ->
            _user.value = localData
        }
    }
    fun updateProfile(
        name: String? = null,
        email: String? = null,
        nip: String? = null
    ) = viewModelScope.launch {
        if (name.isNullOrBlank() && email.isNullOrBlank() && nip.isNullOrBlank()) {
            _errorMessage.value = "Tidak ada perubahan yang disimpan"
            return@launch
        }
        _isLoading.value = true
        repository.updateProfile(name, email, nip)
            .onSuccess {
                if (it.isSuccessful) {
                    _user.value = it.body()?.data
                    _updateSuccess.value = it.body()?.message ?: "Profil berhasil diperbarui"
                } else {
                    _errorMessage.value = "Gagal memperbarui profil"
                }
            }
            .onFailure { _errorMessage.value = it.message }
        _isLoading.value = false
    }
    fun changePassword(current: String, new: String, confirm: String) = viewModelScope.launch {
        when {
            current.isBlank() -> { _errorMessage.value = "Password saat ini tidak boleh kosong"; return@launch }
            new.length < 6    -> { _errorMessage.value = "Password minimal 6 karakter"; return@launch }
            new != confirm    -> { _errorMessage.value = "Konfirmasi password tidak cocok"; return@launch }
        }
        _isLoading.value = true
        repository.changePassword(current, new)
            .onSuccess {
                if (it.isSuccessful && it.body()?.success == true) {
                    _updateSuccess.value = it.body()?.message ?: "Password berhasil diubah"
                } else {
                    _errorMessage.value = it.body()?.message ?: "Gagal mengubah password"
                }
            }
            .onFailure { _errorMessage.value = it.message }
        _isLoading.value = false
    }
    fun clearMessages() {
        _errorMessage.value = null
        _updateSuccess.value = null
    }
}