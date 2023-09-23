package cu.suitetecsa.nautanav.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cu.suitetecsa.nautanav.commons.USER_TABLE
import cu.suitetecsa.nautanav.domain.model.UserModel
import cu.suitetecsa.nautanav.util.NavigationType

@Entity(tableName = USER_TABLE)
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "user_name") var userName: String,
    @ColumnInfo(name = "password") var password: String,
    @ColumnInfo(name = "blocking_date") val blockingDate: String,
    @ColumnInfo(name = "date_of_elimination") val dateOfElimination: String,
    @ColumnInfo(name = "account_type") val accountType: String,
    @ColumnInfo(name = "service_type") val serviceType: NavigationType,
    @ColumnInfo(name = "credit") var credit: Float,
    @ColumnInfo(name = "remaining_time") var remainingTime: Int,
    @ColumnInfo(name = "email") val email: String,
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
) {
    override fun toString(): String {
        return userName
    }
}

fun User.toDomain() = UserModel(
    id = this.id,
    username = this.userName,
    password = this.password,
    blockingDate = this.blockingDate,
    dateOfElimination = this.dateOfElimination,
    accountType = this.accountType,
    serviceType = this.serviceType,
    credit = this.credit,
    remainingTime = this.remainingTime,
    email = this.email,
    offer = this.offer,
    monthlyFee = this.monthlyFee,
    downloadSpeeds = this.downloadSpeed,
    uploadSpeeds = this.uploadSpeed,
    phone = this.phone,
    linkIdentifiers = this.linkIdentifiers,
    linkStatus = this.linkStatus,
    activationDate = this.activationDate,
    blockingDateHome = this.blockingDateHome,
    dateOfEliminationHome = this.dateOfEliminationHome,
    quotePaid = this.quotePaid,
    voucher = this.voucher,
    debt = this.debt
)
