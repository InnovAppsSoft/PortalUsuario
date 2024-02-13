package cu.suitetecsa.cubacelmanager.usecases

import android.annotation.SuppressLint
import android.util.Log
import cu.suitetecsa.cubacelmanager.data.sources.BalanceRepository
import cu.suitetecsa.cubacelmanager.domain.model.Balance
import cu.suitetecsa.sdk.android.balance.FetchBalanceCallBack
import cu.suitetecsa.sdk.android.balance.consult.UssdRequest
import cu.suitetecsa.sdk.android.balance.response.BonusBalance
import cu.suitetecsa.sdk.android.balance.response.DataBalance
import cu.suitetecsa.sdk.android.balance.response.MessagesBalance
import cu.suitetecsa.sdk.android.balance.response.PrincipalBalance
import cu.suitetecsa.sdk.android.balance.response.UssdResponse
import cu.suitetecsa.sdk.android.balance.response.VoiceBalance
import cu.suitetecsa.sdk.android.kotlin.smartFetchBalance
import cu.suitetecsa.sdk.android.model.SimCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "UpdateBalance"

internal class UpdateBalance @Inject constructor(
    private val balanceRepository: BalanceRepository,
    private val getConsultMessage: GetConsultMessage
) {
    @SuppressLint("MissingPermission")
    operator fun invoke(simCard: SimCard, onRequest: (String?) -> Unit, onFinish: () -> Unit) {
        simCard.smartFetchBalance(object : FetchBalanceCallBack {
            override fun onFailure(throwable: Throwable) {
                Log.e(TAG, "onFailure: ", throwable.cause)
                onFinish()
            }

            override fun onFetching(request: UssdRequest) {
                Log.d(TAG, "onRequesting: ${request.ussdCode}")
                onRequest(getConsultMessage(request))
            }

            override fun onSuccess(request: UssdRequest, response: UssdResponse) {
                Log.d(TAG, "onSuccess: ${response.javaClass.name}")
                CoroutineScope(Dispatchers.IO).launch {
                    var balance = balanceRepository
                        .getBalance(simCard.subscriptionId) ?: Balance(simCard.subscriptionId)
                    when (request) {
                        UssdRequest.BONUS_BALANCE -> {
                            balance = balance.copy(
                                bonusCredit = (response as BonusBalance).credit?.balance,
                                bonusCreditDueDate = response.credit?.dueDate,
                                bonusData = response.data?.data,
                                bonusDataDueDate = response.data?.dataLte,
                                bonusDataLte = response.data?.dueDate,
                                bonusDataCu = response.dataCu?.data,
                                bonusDataCuDueDate = response.dataCu?.dueDate,
                                bonusUnlimitedDataDueDate = response.unlimitedData?.dueDate
                            )
                            onRequest(null)
                            onFinish()
                        }
                        UssdRequest.CUSTOM -> {}
                        UssdRequest.DATA_BALANCE -> {
                            balance = balance.copy(
                                data = (response as DataBalance).data,
                                dataLte = response.dataLte,
                                dataRemainingDays = response.remainingDays,
                                mailData = response.mailData?.data,
                                mailDataRemainingDays = response.mailData?.remainingDays,
                                dailyData = response.dailyData?.data,
                                dailyDataRemainingHours = response.dailyData?.remainingHours
                            )
                        }
                        UssdRequest.MESSAGES_BALANCE -> {
                            balance = balance.copy(
                                sms = (response as MessagesBalance).sms.toInt(),
                                smsRemainingDays = response.remainingDays
                            )
                        }
                        UssdRequest.PRINCIPAL_BALANCE -> {
                            balance = balance.copy(
                                credit = (response as PrincipalBalance).balance,
                                activeUntil = response.activeUntil,
                                dueDate = response.dueDate
                            )
                            if (UssdRequest.DATA_BALANCE !in response.consults) {
                                balance = balance.copy(data = null, dataLte = null, dataRemainingDays = null, mailData = null, mailDataRemainingDays = null, dailyData = null, dailyDataRemainingHours = null)
                            }
                            if (UssdRequest.VOICE_BALANCE !in response.consults) {
                                balance = balance.copy(voice = null, voiceRemainingDays = null)
                            }
                            if (UssdRequest.MESSAGES_BALANCE !in response.consults) {
                                balance = balance.copy(sms = null, smsRemainingDays = null)
                            }
                        }
                        UssdRequest.VOICE_BALANCE -> {
                            balance = balance.copy(
                                voice = (response as VoiceBalance).seconds,
                                voiceRemainingDays = response.remainingDays
                            )
                        }
                    }
                    balanceRepository.updateBalance(balance)
                }
            }
        })
    }
}
