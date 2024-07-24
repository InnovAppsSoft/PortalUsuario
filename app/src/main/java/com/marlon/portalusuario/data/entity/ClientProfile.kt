package com.marlon.portalusuario.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marlon.portalusuario.domain.model.ClientProfile as ModelClientProfile

@Entity(tableName = "client_profile")
data class ClientProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val name: String,
    @ColumnInfo(name = "mail_notifications") val mailNotifications: Boolean,
    @ColumnInfo(name = "mobile_notifications") val mobileNotifications: Boolean,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "portal_user") val portalUser: String,
    @ColumnInfo(name = "last_update") val lastUpdate: String,
)

fun ClientProfile.asModel() = ModelClientProfile(
    this.email,
    this.name,
    this.mailNotifications,
    this.mobileNotifications,
    this.phoneNumber,
    this.portalUser,
    this.lastUpdate
)
