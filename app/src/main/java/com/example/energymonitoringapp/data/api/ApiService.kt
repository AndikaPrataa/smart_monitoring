package com.example.energymonitoringapp.data.api


import com.example.energymonitoringapp.data.response.AllNotifResponseItem
import com.example.energymonitoringapp.data.response.AssignTeknisiRequest
import com.example.energymonitoringapp.data.response.AssignTeknisiResponse
import com.example.energymonitoringapp.data.response.ChangePasswordRequest
import com.example.energymonitoringapp.data.response.ChangePasswordResponse
import com.example.energymonitoringapp.data.response.DayaListrikResponse
import com.example.energymonitoringapp.data.response.DetailNotifAdminResponse
import com.example.energymonitoringapp.data.response.DetailNotifTeknisiResponse
import com.example.energymonitoringapp.data.response.KonfirmasiResponse
import com.example.energymonitoringapp.data.response.KonsumsiEnergiResponse
import com.example.energymonitoringapp.data.response.LingkunganResponse
import com.example.energymonitoringapp.data.response.ListTeknisiResponse
import com.example.energymonitoringapp.data.response.ListrikResponse
import com.example.energymonitoringapp.data.response.LoginRequest
import com.example.energymonitoringapp.data.response.LoginResponse
import com.example.energymonitoringapp.data.response.NotifAdminResponse
import com.example.energymonitoringapp.data.response.NotifTeknisiResponse
import com.example.energymonitoringapp.data.response.UpdateProfileRequest
import com.example.energymonitoringapp.data.response.UpdateProfileResponse
import com.example.energymonitoringapp.data.response.UserDataResponse
import com.example.energymonitoringapp.pushNotif.FcmTokenRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("auth/me")
    suspend fun getProfile(): Response<UserDataResponse>

    @PUT("auth/update-profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<UpdateProfileResponse>

    @PUT("auth/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<ChangePasswordResponse>

    @POST("auth/fcm-token")
    suspend fun registerFcmToken(
        @Body request: FcmTokenRequest
    ): Response<Unit>

    @GET("notifications")
    suspend fun getAllNotifications(): Response<List<AllNotifResponseItem>>
    @GET("monitoring/all")
    suspend fun getMonitoringAll(): Response<LingkunganResponse>
    @GET("listrik/all")
    suspend fun getListrikAll(): Response<ListrikResponse>
    @GET("daya/all")
    suspend fun getDayaAll(): Response<DayaListrikResponse>
    @GET("daya/ike")
    suspend fun getKonsumsiEnergi(): Response<KonsumsiEnergiResponse>

    @GET("admin/notifications")
    suspend fun getAdminNotifications(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("status")   status: String? = null
    ): Response<NotifAdminResponse>

    @GET("admin/notifications/{id}")
    suspend fun getAdminNotificationDetail(
        @Path("id") id: Int
    ): Response<DetailNotifAdminResponse>

    @GET("admin/teknisi")
    suspend fun getListTeknisi(): Response<ListTeknisiResponse>

    @POST("admin/notifications/{id}/assign")
    suspend fun assignTeknisi(
        @Path("id") notifId: Int,
        @Body request: AssignTeknisiRequest
    ): Response<AssignTeknisiResponse>

    @GET("technician/notifications")
    suspend fun getTeknisiNotifications(): Response<NotifTeknisiResponse>

    @GET("technician/notifications/{id}")
    suspend fun getTeknisiNotificationDetail(
        @Path("id") id: Int
    ): Response<DetailNotifTeknisiResponse>

    @Multipart
    @POST("technician/notifications/{id}/complete")
    suspend fun konfirmasiPenanganan(
        @Path("id") notifId: Int,
        @Part("action_taken") actionTaken: RequestBody,
        @Part("additional_note") additionalNote: RequestBody?,
        @Part fieldPhoto: MultipartBody.Part?
    ): Response<KonfirmasiResponse>
}