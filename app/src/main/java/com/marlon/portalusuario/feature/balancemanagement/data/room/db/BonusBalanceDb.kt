package com.marlon.portalusuario.feature.balancemanagement.data.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marlon.portalusuario.feature.balancemanagement.data.room.dao.BonusBalanceDao
import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.BonusBalanceCacheEntity

@Database(entities = [BonusBalanceCacheEntity::class], version = 1, exportSchema = false)
internal abstract class BonusBalanceDb : RoomDatabase() {
    abstract val bonusBalanceDao: BonusBalanceDao
}
