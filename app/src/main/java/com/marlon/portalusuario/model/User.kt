package com.marlon.portalusuario.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var username: String = "prp",
    var password: String = "",
    var nautaEmailPassword: String = "",
    var attributeUuid: String = "",
    var csrfhw: String = "",
    var accountNavegationType: Int = 0,
    var leftTime: String = "",
    var accountCredit: String = "",
    var accountState: String = "",
    var lastConnectionDateTime: Long = 0L,
    var blockDate: String = "",
    var delDate: String = "",
    var accountType: String = "",
    var serviceType: String = "",
    var emailAccount: String = "",
) : Serializable {
    val fullUsername: String
        get() = username + if (accountNavegationType == 0) "@nauta.com.cu" else "@nauta.co.cu"

    override fun toString(): String = username

    companion object {
        private const val serialVersionUID = 1L
    }
}
