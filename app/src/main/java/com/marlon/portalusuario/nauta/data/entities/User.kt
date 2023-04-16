package com.marlon.portalusuario.nauta.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marlon.portalusuario.commons.NavigationType
import com.marlon.portalusuario.commons.USER_TABLE

@Entity(tableName = USER_TABLE)
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "user_name") var userName: String,
    @ColumnInfo(name = "password") var password: String,
    @ColumnInfo(name = "account_navigation_type") var accountNavigationType: NavigationType,
    @ColumnInfo(name = "last_connection") val lastConnection: Long,
    @ColumnInfo(name = "blocking_date") val blockingDate: String,
    @ColumnInfo(name = "date_of_elimination") val dateOfElimination: String,
    @ColumnInfo(name = "account_type") val accountType: String,
    @ColumnInfo(name = "service_type") val serviceType: String,
    @ColumnInfo(name = "credit") val credit: String,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "mail_account") val mailAccount: String,
    @ColumnInfo(name = "offer") val offer: String? = null,
    @ColumnInfo(name = "monthly_fee") val monthlyFee: String? = null,
    @ColumnInfo(name = "download_speed") val downloadSpeed: String? = null,
    @ColumnInfo(name = "upload_speed") val uploadSpeed: String? = null,
    @ColumnInfo(name = "phone") val phone: String? = null,
    @ColumnInfo(name = "link_identifiers") val linkIdentifiers: String? = null,
    @ColumnInfo(name = "link_status") val linkStatus: String? = null,
    @ColumnInfo(name = "activation_date") val activationDate: String? = null,
    @ColumnInfo(name = "blocking_date_home") val blockingDateHome: String? = null,
    @ColumnInfo(name = "date_of_elimination_home") val dateOfEliminationHome: String? = null,
    @ColumnInfo(name = "quote_paid") val quotePaid: String? = null,
    @ColumnInfo(name = "voucher") val voucher: String? = null,
    @ColumnInfo(name = "debt") val debt: String? = null
)
