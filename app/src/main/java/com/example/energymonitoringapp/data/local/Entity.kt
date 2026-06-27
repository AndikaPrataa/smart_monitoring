package com.example.energymonitoringapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensor_lingkungan")
data class SensorLingkunganEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val suhu: Double? = null,
    val kelembapan: Double? = null,
    val pm25: Double? = null,
    val pm10: Double? = null,
    val gasCo: Double? = null,
    val gasCo2: Double? = null,
    val tvoc: Double? = null,
    val cahaya: Double? = null,
    val kebisingan: Double? = null,
    val lokasi: String? = null,
    val waktu: String? = null,
    val ieqScore: Int? = null,
    val ieqStatus: String? = null,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sensor_listrik")
data class SensorListrikEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val totalVoltage: Double? = null,
    val totalCurrent: Double? = null,
    val timeStamp: String? = null,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sensor_daya")
data class SensorDayaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val totalActivePower: Double? = null,
    val unit: String? = null,
    val energyKwh: Double? = null,
    val biaya: Double? = null,
    val timeStamp: String? = null,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifikasi")
data class NotifEntity(
    @PrimaryKey
    val id            : Int,
    val role          : String  = "all",
    val kategori      : String? = null,
    val sensor        : String? = null,
    val title         : String? = null,
    val level         : String? = null,
    val value         : Double? = null,
    val unit          : String? = null,
    val message       : String? = null,
    val lokasi        : String? = null,
    val status        : String? = null,
    val assignedTo    : Int?    = null,
    val assignedAt    : String? = null,
    val completedAt   : String? = null,
    val fieldPhoto    : String? = null,
    val actionTaken   : String? = null,
    val additionalNote: String? = null,
    val fieldPhotoUrl : String? = null,
    val timestamp     : String? = null,
    val savedAt       : Long    = System.currentTimeMillis()
)

@Entity(tableName = "list_teknisi")
data class TeknisiEntity(
    @PrimaryKey
    val id                  : Int,
    val name                : String? = null,
    val email               : String? = null,
    val nip                 : String? = null,
    val role                : String? = null,
    val statusTeknisi       : String? = null,
    val canReceiveTask      : Boolean? = null,
    val activeTaskCount     : Int?    = null,
    val savedAt             : Long    = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int,
    val name: String? = null,
    val email: String? = null,
    val nip: String? = null,
    val role: String? = null,
    val createdAt: String? = null,
    val savedAt: Long = System.currentTimeMillis()
)