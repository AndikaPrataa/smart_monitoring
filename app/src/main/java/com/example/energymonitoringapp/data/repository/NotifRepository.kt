package com.example.energymonitoringapp.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.energymonitoringapp.data.api.ApiService
import com.example.energymonitoringapp.data.local.NotifEntity
import com.example.energymonitoringapp.data.local.SensorDao
import com.example.energymonitoringapp.data.local.TeknisiEntity
import com.example.energymonitoringapp.data.AdminNotifPagingSource
import com.example.energymonitoringapp.data.response.AssignTeknisiRequest
import com.example.energymonitoringapp.data.response.NotifAdminItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotifRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: ApiService,
    private val dao: SensorDao,
) {
    suspend fun fetchAdminNotif() = runCatching {
        api.getAdminNotifications(page = 1, perPage = 20).also { response ->
            if (response.isSuccessful) {
                val entities = response.body()?.data?.filterNotNull()?.map { item ->
                    NotifEntity(
                        id         = item.id ?: 0,
                        role       = "admin",
                        kategori   = item.kategori,
                        sensor     = item.sensor,
                        title      = item.title,
                        level      = item.level,
                        value      = item.value?.toDouble(),
                        unit       = item.unit,
                        message    = item.message,
                        lokasi     = item.lokasi,
                        status     = item.status,
                        assignedTo = (item.assignedTo as? Double)?.toInt()
                            ?: (item.assignedTo as? Int),
                        assignedAt = item.assignedAt?.toString(),
                        timestamp  = item.timestamp
                    )
                } ?: emptyList()
                if (entities.isNotEmpty()) dao.saveAllNotif(entities)
            }
        }
    }
    suspend fun fetchAdminNotifDetail(id: Int) = runCatching {
        api.getAdminNotificationDetail(id)
    }
    suspend fun assignTeknisi(notifId: Int, teknisiId: Int) = runCatching {
        api.assignTeknisi(
            notifId = notifId,
            request = AssignTeknisiRequest(assignedTo = teknisiId)
        )
    }
    suspend fun fetchListTeknisi() = runCatching {
        api.getListTeknisi().also { response ->
            if (response.isSuccessful) {
                val entities = response.body()?.data?.filterNotNull()?.map { item ->
                    TeknisiEntity(
                        id              = item.id ?: 0,
                        name            = item.name,
                        email           = item.email,
                        nip             = item.nip,
                        role            = item.role,
                        statusTeknisi   = item.statusTeknisi,
                        canReceiveTask  = item.canReceiveTask,
                        activeTaskCount = item.activeTaskCount
                    )
                } ?: emptyList()
                if (entities.isNotEmpty()) dao.saveAllTeknisi(entities)
            }
        }
    }
    suspend fun fetchTeknisiNotif() = runCatching {
        api.getTeknisiNotifications().also { response ->
            if (response.isSuccessful) {
                val entities = response.body()?.data?.filterNotNull()?.map { item ->
                    NotifEntity(
                        id             = item.id ?: 0,
                        role           = "teknisi",
                        kategori       = item.kategori,
                        sensor         = item.sensor,
                        title          = item.title,
                        level          = item.level,
                        value          = item.value?.toDouble(),
                        unit           = item.unit,
                        message        = item.message,
                        lokasi         = item.lokasi,
                        status         = item.status,
                        assignedTo     = item.assignedTo,
                        assignedAt     = item.assignedAt,
                        completedAt    = item.completedAt,
                        fieldPhoto     = item.fieldPhoto,
                        actionTaken    = item.actionTaken,
                        additionalNote = item.additionalNote,
                        fieldPhotoUrl  = item.fieldPhotoUrl,
                        timestamp      = item.timestamp
                    )
                } ?: emptyList()
                if (entities.isNotEmpty()) dao.saveAllNotif(entities)
            }
        }
    }
    suspend fun fetchTeknisiNotifDetail(id: Int) = runCatching {
        api.getTeknisiNotificationDetail(id)
    }

    suspend fun konfirmasiPenanganan(
        notifId       : Int,
        fotoUri       : Uri?,
        actionTaken   : String,
        additionalNote: String
    ) = runCatching {
        val actionPart = actionTaken.toRequestBody("text/plain".toMediaTypeOrNull())
        val notePart   = additionalNote.toRequestBody("text/plain".toMediaTypeOrNull())

        val fotoPart: MultipartBody.Part? = fotoUri?.let { uri ->
            try {
                val tempFile = File.createTempFile("foto_", ".jpg", context.cacheDir)

                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri) }

                FileOutputStream(tempFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out) }

                if (tempFile.length() == 0L) return@let null

                val requestBody = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val fileName = "foto_${System.currentTimeMillis()}.jpg"
                MultipartBody.Part.createFormData("field_photo", fileName, requestBody)

            } catch (e: Exception) {
                null }
        }

        api.konfirmasiPenanganan(
            notifId        = notifId,
            actionTaken    = actionPart,
            additionalNote = notePart,
            fieldPhoto     = fotoPart
        )
    }

    fun getLocalNotifAdmin()   : Flow<List<NotifEntity>>   = dao.getNotifByRole("admin")
    fun getLocalNotifTeknisi() : Flow<List<NotifEntity>>   = dao.getNotifByRole("teknisi")
    fun getLocalTeknisi()      : Flow<List<TeknisiEntity>> = dao.getAllTeknisi()

    suspend fun getLocalNotifAdminById(id: Int): NotifEntity? =
        dao.getNotifByIdAndRole(id, "admin")
    suspend fun getLocalNotifTeknisiById(id: Int): NotifEntity? =
        dao.getNotifByIdAndRole(id, "teknisi")

    fun getPagingAdminNotif(status: String? = null): Flow<PagingData<NotifAdminItem>> {
        return Pager(
            config = PagingConfig(
                pageSize        = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { AdminNotifPagingSource(api, dao, status) }
        ).flow
    }

    suspend fun getLocalNotifAdminOnce()   : List<NotifEntity> = dao.getNotifByRoleOnce("admin")
    suspend fun getLocalNotifTeknisiOnce() : List<NotifEntity> = dao.getNotifByRoleOnce("teknisi")
}