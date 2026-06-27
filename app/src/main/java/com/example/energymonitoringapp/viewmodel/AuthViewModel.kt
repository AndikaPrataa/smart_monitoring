package com.example.energymonitoringapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.energymonitoringapp.data.SessionManager
import com.example.energymonitoringapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle    : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val role: String, val name: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val session: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String, expectedRole: String) =
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.login(email, password)
                .onSuccess { response ->
                    val actualRole = response.data?.role ?: ""
                    if (actualRole != expectedRole) {
                        repository.logout()
                        val roleName = if (expectedRole == SessionManager.ROLE_ADMIN)
                            "Admin" else "Teknisi"
                        _uiState.value = AuthUiState.Error(
                            "Akun ini bukan akun $roleName. "
                        )
                    } else {
                        _uiState.value = AuthUiState.Success(
                            role = actualRole,
                            name = response.data?.name ?: ""
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Login gagal")
                }
        }
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}