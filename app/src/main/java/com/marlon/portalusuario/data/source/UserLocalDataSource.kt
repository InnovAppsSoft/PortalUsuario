package com.marlon.portalusuario.data.source

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.marlon.portalusuario.data.entity.MobileService
import com.marlon.portalusuario.domain.model.MobileBonus
import com.marlon.portalusuario.domain.model.MobilePlan
import com.marlon.portalusuario.domain.model.ServiceType.Local
import com.marlon.portalusuario.domain.model.ServiceType.LocalAndRemote
import io.github.suitetecsa.sdk.android.balance.FetchBalanceCallBack
import io.github.suitetecsa.sdk.android.balance.RequestState
import io.github.suitetecsa.sdk.android.balance.consult.UssdRequest
import io.github.suitetecsa.sdk.android.balance.response.BonusBalance
import io.github.suitetecsa.sdk.android.balance.response.DataBalance
import io.github.suitetecsa.sdk.android.balance.response.MessagesBalance
import io.github.suitetecsa.sdk.android.balance.response.PrincipalBalance
import io.github.suitetecsa.sdk.android.balance.response.UssdResponse
import io.github.suitetecsa.sdk.android.balance.response.VoiceBalance
import io.github.suitetecsa.sdk.android.model.SimCard
import io.github.suitetecsa.sdk.android.utils.fixDate
import io.github.suitetecsa.sdk.android.utils.isActive
import io.github.suitetecsa.sdk.android.utils.smartFetchBalance
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "UserLocalDataSource"

class UserLocalDataSource {
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchUserFromSimCard(
        simCard: SimCard,
        isRemote: Boolean = false,
        callback: (MobileService) -> Unit
    ) {
        Log.d(TAG, "fetchUserFromLocal: Fetching user from local using sim card")
        simCard.smartFetchBalance(object : FetchBalanceCallBack {
            private lateinit var ussdRequest: UssdRequest
            var mobService = MobileService(
                "53${simCard.phoneNumber!!}",
                false,
                "",
                "Activo",
                "",
                "",
                "",
                false,
                emptyList(),
                emptyList(),
                "CUP",
                simCard.phoneNumber!!,
                "",
                false,
                simCard.slotIndex,
                if (isRemote) LocalAndRemote else Local
            )

            private fun addToPlanList(data: String, type: String, expires: String) {
                val dateExpires = expires.takeIf { it.isActive }?.fixDate() ?: "No activos"
                mobService = mobService.copy(
                    plans = mobService.plans.toMutableList()
                        .apply { add(MobilePlan(data, type, dateExpires)) }
                )
            }

            private fun addToBonusList(data: String, type: String, expires: String) {
                val dateExpires = expires.takeIf { it.isActive }?.fixDate() ?: "No activos"
                mobService = mobService.copy(
                    bonuses = mobService.bonuses.toMutableList()
                        .apply { add(MobileBonus(data, "", type, dateExpires)) }
                )
            }

            override fun onFailure(throwable: Throwable) {
                if (ussdRequest == UssdRequest.BONUS_BALANCE) {
                    callback(mobService)
                }
            }

            override fun onStateChanged(
                request: UssdRequest,
                state: RequestState,
                retryCount: Int
            ) {
                Log.d(TAG, "onFetching: ${request.name}")
            }

            override fun onSuccess(request: UssdRequest, response: UssdResponse) {
                when (request) {
                    UssdRequest.PRINCIPAL_BALANCE -> {
                        mobService = mobService.copy(
                            lockDate = (response as PrincipalBalance).blockDate.fixDate(),
                            deletionDate = response.deletionDate.fixDate(),
                            mainBalance = response.balance
                        )
                    }
                    UssdRequest.DATA_BALANCE -> {
                        (response as DataBalance).let { resp ->
                            mobService = mobService.copy(consumptionRate = resp.usageBasedPricing)
                            resp.data?.let { addToPlanList(it, "DATOS", resp.expires!!) }
                            resp.dataLte?.let { addToPlanList(it, "DATOS LTE", resp.expires!!) }
                            resp.mailData?.let {
                                addToPlanList(
                                    it.data,
                                    "BOLSA MENSAJERIA",
                                    resp.expires!!
                                )
                            }
                            resp.dailyData?.let {
                                addToPlanList(
                                    it.data,
                                    "BOLSA DIARIA",
                                    resp.expires!!
                                )
                            }
                        }
                    }
                    UssdRequest.VOICE_BALANCE ->
                        addToPlanList((response as VoiceBalance).data, "MINUTOS", response.expires)
                    UssdRequest.MESSAGES_BALANCE ->
                        addToPlanList((response as MessagesBalance).data, "SMS", response.expires)
                    UssdRequest.BONUS_BALANCE -> {
                        (response as BonusBalance).let { resp ->
                            resp.credit?.let { addToBonusList(it.data, "SALDO", it.expires) }
                            resp.voice?.let { addToBonusList(it.data, "MINUTOS", it.expires) }
                            resp.sms?.let { addToBonusList(it.data, "SMS", it.expires) }
                            resp.data?.let { data ->
                                data.data?.let { addToBonusList(it, "DATOS", data.expires!!) }
                                data.dataLte?.let { addToBonusList(it, "DATOS LTE", data.expires!!) }
                            }
                            resp.dataCu?.let { addToBonusList(it.data, "DATOS CU", it.expires) }
                            resp.unlimitedData?.let {
                                addToBonusList(
                                    "",
                                    "DATOS ILIMITADOS",
                                    it.expires
                                )
                            }
                        }
                        callback(mobService)
                    }
                    UssdRequest.CUSTOM -> {}
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetch(simCard: SimCard, isRemote: Boolean): MobileService =
        suspendCoroutine { continuation ->
            fetchUserFromSimCard(simCard, isRemote) { continuation.resume(it) }
        }
}
