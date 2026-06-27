package com.example.energymonitoringapp.di

import android.content.Context
import androidx.room.Room
import com.example.energymonitoringapp.data.local.AppDatabase
import com.example.energymonitoringapp.data.local.SensorDao
import com.example.energymonitoringapp.data.local.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "energy_monitoring_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    @Provides
    @Singleton
    fun provideSensorDao(database: AppDatabase): SensorDao =
        database.sensorDao()
    @Provides
    @Singleton
    fun provideUserProfileDao(database: AppDatabase): UserProfileDao =
        database.userProfileDao()
}
