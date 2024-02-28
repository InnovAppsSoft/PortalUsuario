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
    val bonusCreditRemainingDays: Int?,
    val bonusUnlimitedDataRemainingDays: Int?,
    val bonusData: Long?,
    val bonusDataLte: Long?,
    val bonusDataRemainingDays: Int?,
    val bonusVoice: Long?,
    val bonusVoiceRemainingDays: Int?,
    val bonusSms: Int?,
    val bonusSmsRemainingDays: Int?,
    val bonusDataCu: Long?,
    val bonusDataCuRemainingDays: Int?,
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
    bonusCreditRemainingDays = this.bonusCreditRemainingDays,
    bonusUnlimitedDataRemainingDays = this.bonusUnlimitedDataRemainingDays,
    bonusData = this.bonusData,
    bonusDataLte = this.bonusDataLte,
    bonusDataRemainingDays = this.bonusDataRemainingDays,
    bonusVoice = this.bonusVoice,
    bonusVoiceRemainingDays = this.bonusVoiceRemainingDays,
    bonusSms = this.bonusSms,
    bonusSmsRemainingDays = this.bonusSmsRemainingDays,
    bonusDataCu = this.bonusDataCu,
    bonusDataCuRemainingDays = this.bonusDataCuRemainingDays
)
