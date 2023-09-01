package com.marlon.portalusuario.feature.balancemanagement.data.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marlon.portalusuario.feature.balancemanagement.data.room.dao.MainBalanceDao
import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.MainBalanceCacheEntity

@Database(entities = [MainBalanceCacheEntity::class], version = 1, exportSchema = false)
internal abstract class MainBalanceDb : RoomDatabase() {
    abstract val mainBalanceDao: MainBalanceDao
}
