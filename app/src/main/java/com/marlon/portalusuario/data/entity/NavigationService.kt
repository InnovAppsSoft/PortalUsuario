package com.marlon.portalusuario.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marlon.portalusuario.domain.model.NavigationService as ModelNavigationService

@Entity(tableName = "navigation_service")
data class NavigationService(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "bonus_to_enjoy") val bonusToEnjoy: String,
    @ColumnInfo(name = "access_account") val accessAccount: String,
    val status: String,
    @ColumnInfo(name = "lock_date") val lockDate: String,
    @ColumnInfo(name = "deletion_date") val deletionDate: String,
    @ColumnInfo(name = "sale_date") val saleDate: String,
    @ColumnInfo(name = "bonus_hours") val bonusHours: String,
    val currency: String,
    val balance: String,
    @ColumnInfo(name = "access_type") val accessType: String,
    @ColumnInfo(name = "product_type") val productType: String,
)

fun NavigationService.asModel() = ModelNavigationService(
    id,
    bonusToEnjoy,
    accessAccount,
    status,
    lockDate,
    deletionDate,
    saleDate,
    bonusHours,
    currency,
    balance,
    accessType,
    productType
)
