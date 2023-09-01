package com.marlon.portalusuario.feature.balancemanagement.data.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marlon.portalusuario.feature.balancemanagement.data.room.util.MAIN_BALANCE_TABLE

@Entity(tableName = MAIN_BALANCE_TABLE)
data class MainBalanceCacheEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val credit: Float?,
    @Embedded val data: MainDataCacheEntity,
    @Embedded val voice: MainVoiceCacheEntity,
    @Embedded val sms: MainSmsCacheEntity,
    @Embedded val daily: DailyDataCacheEntity,
    @Embedded val mail: MailDataCacheEntity,
    val activeUntil: String?,
    val dueDate: String?
)