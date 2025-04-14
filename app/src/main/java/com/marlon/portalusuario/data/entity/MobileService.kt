package com.marlon.portalusuario.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marlon.portalusuario.domain.model.MobileBonus
import com.marlon.portalusuario.domain.model.MobilePlan
import com.marlon.portalusuario.domain.model.ServiceType

@Entity(tableName = "mobile_service")
data class MobileService(
    @PrimaryKey val id: String,
    val lte: Boolean,
    @ColumnInfo(name = "advance_balance") val advanceBalance: String,
    val status: String,
    @ColumnInfo(name = "lock_date") val lockDate: String,
    @ColumnInfo(name = "deletion_date") val deletionDate: String,
    @ColumnInfo(name = "sale_date") val saleDate: String,
    val internet: Boolean,
    val plans: List<MobilePlan>,
    val bonuses: List<MobileBonus>,
    val currency: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "main_balance") val mainBalance: String,
    @ColumnInfo(name = "consumption_rate") val consumptionRate: Boolean,
    @ColumnInfo(name = "slot_index") val slotIndex: Int = -1,
    @ColumnInfo(name = "service_type") val type: ServiceType = ServiceType.Local,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long = 0,
)
