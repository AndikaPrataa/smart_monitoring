package com.example.energymonitoringapp.data.repository

import com.example.energymonitoringapp.data.SessionManager
import com.example.energymonitoringapp.data.api.ApiService
import com.example.energymonitoringapp.data.local.UserProfileDao
import com.example.energymonitoringapp.data.local.UserProfileEntity
import com.example.energymonitoringapp.data.response.ChangePasswordRequest
import com.example.energymonitoringapp.data.response.UpdateProfileRequest
import com.example.energymonitoringapp.data.response.UserData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: ApiService,
    private val session: SessionManager,
    private val profileDao: UserProfileDao
) {
    suspend fun getProfile() = runCatching {
        val response = api.getProfile()
        if (response.isSuccessful) {
            response.body()?.data?.let { data ->
                profileDao.saveProfile(
                    UserProfileEntity(
                        id        = data.id ?: 0,
                        name      = data.name,
                        email     = data.email,
                        nip       = data.nip,
                        role      = data.role,
                        createdAt = data.createdAt
                    )
                )
            }
        }
        response
    }
    suspend fun getLocalProfile(): UserData? {
        return profileDao.getProfile()?.let { entity ->
            UserData(
                id        = entity.id,
                name      = entity.name,
                email     = entity.email,
                nip       = entity.nip,
                role      = entity.role,
                createdAt = entity.createdAt
            )
        }
    }
    suspend fun updateProfile(
        name: String? = null,
        email: String? = null,
        nip: String? = null
    ) = runCatching {
        val response = api.updateProfile(
            UpdateProfileRequest(
                name  = name?.takeIf  { it.isNotBlank() },
                email = email?.takeIf { it.isNotBlank() },
                nip   = nip?.takeIf   { it.isNotBlank() }
            )
        )
        if (response.isSuccessful) {
            response.body()?.data?.let { data ->
                profileDao.saveProfile(
                    UserProfileEntity(
                        id        = data.id ?: 0,
                        name      = data.name,
                        email     = data.email,
                        nip       = data.nip,
                        role      = data.role,
                        createdAt = data.createdAt
                    )
                )
                session.saveProfileData(
                    name  = data.name,
                    email = data.email,
                    nip   = data.nip
                )
            }
        }
        response
    }
    suspend fun changePassword(current: String, new: String) = runCatching {
        val response = api.changePassword(
            ChangePasswordRequest(current, new, new)
        )
        if (response.isSuccessful) {
            response.body()?.token?.let { newToken ->
                session.saveToken(newToken)
            }
        }
        response
    }
}