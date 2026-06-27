package com.example.energymonitoringapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLingkungan(data: SensorLingkunganEntity)

    @Query("SELECT * FROM sensor_lingkungan ORDER BY savedAt DESC LIMIT 1")
    fun getLatestLingkungan(): Flow<SensorLingkunganEntity?>

    @Query("DELETE FROM sensor_lingkungan WHERE savedAt < :threshold")
    suspend fun deleteOldLingkungan(threshold: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveListrik(data: SensorListrikEntity)

    @Query("SELECT * FROM sensor_listrik ORDER BY savedAt DESC LIMIT 1")
    fun getLatestListrik(): Flow<SensorListrikEntity?>

    @Query("DELETE FROM sensor_listrik WHERE savedAt < :threshold")
    suspend fun deleteOldListrik(threshold: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDaya(data: SensorDayaEntity)

    @Query("SELECT * FROM sensor_daya ORDER BY savedAt DESC LIMIT 1")
    fun getLatestDaya(): Flow<SensorDayaEntity?>

    @Query("DELETE FROM sensor_daya WHERE savedAt < :threshold")
    suspend fun deleteOldDaya(threshold: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllNotif(list: List<NotifEntity>)

    @Query("SELECT * FROM notifikasi WHERE role = :role ORDER BY savedAt DESC")
    fun getNotifByRole(role: String): Flow<List<NotifEntity>>

    @Query("SELECT * FROM notifikasi WHERE role = :role ORDER BY savedAt DESC")
    suspend fun getNotifByRoleOnce(role: String): List<NotifEntity>

    @Query("SELECT * FROM notifikasi WHERE id = :id AND role = :role LIMIT 1")
    suspend fun getNotifByIdAndRole(id: Int, role: String): NotifEntity?

    @Query("DELETE FROM notifikasi WHERE role = :role")
    suspend fun clearNotifByRole(role: String)

    @Query("DELETE FROM notifikasi")
    suspend fun clearAllNotif()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllTeknisi(list: List<TeknisiEntity>)

    @Query("SELECT * FROM list_teknisi ORDER BY name ASC")
    fun getAllTeknisi(): Flow<List<TeknisiEntity>>

}

