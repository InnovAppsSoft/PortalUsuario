package cu.suitetecsa.cubacelmanager.data.entyties

import androidx.room.Entity
import androidx.room.PrimaryKey
import cu.suitetecsa.cubacelmanager.domain.model.Balance as ModelBalance

@Entity(tableName = "balance_table")
data class Balance(
    @PrimaryKey val subscriptionID: Int,
    val credit: Double,
    val activeUntil: Long,
    val dueDate: Long,
    val usageBasedPricing: Boolean,
    val data: Long?,
    val dataLte: Long?,
    val dataRemainingDays: Int?,
    val dailyData: Long?,
    val dailyDataRemainingHours: Int?,
    val mailData: Long?,
    val mailDataRemainingDays: Int?,
    val voice: Long?,
    val voiceRemainingDays: Int?,
    val sms: Int?,
    val smsRemainingDays: Int?,
    val bonusCredit: Double?,
    val bonusCreditDueDate: Long?,
    val bonusUnlimitedDataDueDate: Long?,
    val bonusData: Long?,
    val bonusDataLte: Long?,
    val bonusDataDueDate: Long?,
    val bonusDataCu: Long?,
    val bonusDataCuDueDate: Long?,
)

internal fun Balance.toDomain() = ModelBalance(
    subscriptionID = this.subscriptionID,
    credit = this.credit,
    activeUntil = this.activeUntil,
    dueDate = this.dueDate,
    usageBasedPricing = this.usageBasedPricing,
    data = this.data,
    dataLte = this.dataLte,
    dataRemainingDays = this.dataRemainingDays,
    dailyData = this.dailyData,
    dailyDataRemainingHours = this.dailyDataRemainingHours,
    mailData = this.mailData,
    mailDataRemainingDays = this.mailDataRemainingDays,
    voice = this.voice,
    voiceRemainingDays = this.voiceRemainingDays,
    sms = this.sms,
    smsRemainingDays = this.smsRemainingDays,
    bonusCredit = this.bonusCredit,
    bonusCreditDueDate = this.bonusCreditDueDate,
    bonusUnlimitedDataDueDate = this.bonusUnlimitedDataDueDate,
    bonusData = this.bonusData,
    bonusDataLte = this.bonusDataLte,
    bonusDataDueDate = this.bonusDataDueDate,
    bonusDataCu = this.bonusDataCu,
    bonusDataCuDueDate = this.bonusDataCuDueDate
)
