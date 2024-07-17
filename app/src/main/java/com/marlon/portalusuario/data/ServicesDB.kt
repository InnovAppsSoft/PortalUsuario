package com.marlon.portalusuario.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.marlon.portalusuario.data.entity.ClientProfile
import com.marlon.portalusuario.data.entity.Converters
import com.marlon.portalusuario.data.entity.MobileService
import com.marlon.portalusuario.data.entity.NavigationService

@Database(entities = [ClientProfile::class, MobileService::class, NavigationService::class], version = 1)
@TypeConverters(Converters::class)
abstract class ServicesDB : RoomDatabase()  {
    abstract val servicesDao: ServicesDao
}