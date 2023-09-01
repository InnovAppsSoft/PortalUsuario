package com.marlon.portalusuario.feature.balancemanagement.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marlon.portalusuario.feature.balancemanagement.data.room.util.BONUS_BALANCE_TABLE
import cu.suitetecsa.sdk.ussd.model.BonusCredit
import cu.suitetecsa.sdk.ussd.model.BonusData
import cu.suitetecsa.sdk.ussd.model.BonusDataCU
import cu.suitetecsa.sdk.ussd.model.BonusUnlimitedData

@Entity(tableName = BONUS_BALANCE_TABLE)
data class BonusBalanceCacheEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @Embedded val credit: BonusCredit,
    @Embedded val unlimitedData: BonusUnlimitedData,
    @Embedded val data: BonusData,
    @Embedded val dataCu: BonusDataCU
)