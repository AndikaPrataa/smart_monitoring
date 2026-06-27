package com.example.energymonitoringapp.data.repository

import com.example.energymonitoringapp.data.SessionManager
import com.example.energymonitoringapp.data.api.ApiService
import com.example.energymonitoringapp.data.response.LoginErrorResponse
import com.example.energymonitoringapp.data.response.LoginRequest
import com.example.energymonitoringapp.data.response.LoginResponse
import com.example.energymonitoringapp.pushNotif.FcmTokenRequest
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val session: SessionManager
) {
    private val gson = Gson()
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return runCatching {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data!!
                val token = data.token ?: ""
                session.saveSession(
                    token = token,
                    role  = data.role  ?: "",
                    name  = data.name  ?: ""
                )
                val fcmToken = session.getFcmToken()
                if (fcmToken.isNotBlank()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        runCatching {
                            api.registerFcmToken(
                                request = FcmTokenRequest(fcmToken = fcmToken)
                            )
                        }
                    }
                }

                response.body()!!
            } else {
                val errBody = response.errorBody()?.string()
                val errMsg  = runCatching {
                    val err = gson.fromJson(errBody, LoginErrorResponse::class.java)
                    err.errors?.email?.firstOrNull()
                        ?: err.errors?.password?.firstOrNull()
                        ?: err.message
                }.getOrNull() ?: "Login gagal"
                throw Exception(errMsg)
            }
        }
    }
    fun logout() {
        session.clearSession()
    }
    fun isLoggedIn() = session.isLoggedIn()
    fun getRole() = session.getRole()
    fun getName() = session.getName()
}