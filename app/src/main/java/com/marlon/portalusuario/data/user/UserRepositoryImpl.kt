package com.marlon.portalusuario.data.user

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.marlon.portalusuario.data.ServicesDao
import com.marlon.portalusuario.data.entity.asModel
import com.marlon.portalusuario.data.mappers.ClientProfileMapper
import com.marlon.portalusuario.data.mappers.MobServMapper
import com.marlon.portalusuario.data.mappers.NavServMapper
import com.marlon.portalusuario.data.preferences.SessionStorage
import com.marlon.portalusuario.data.source.UserApiDataSource
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.domain.model.ClientProfile
import com.marlon.portalusuario.domain.model.DataSession
import com.marlon.portalusuario.domain.model.MobileBonus
import com.marlon.portalusuario.domain.model.MobilePlan
import com.marlon.portalusuario.domain.model.NavigationService
import com.marlon.portalusuario.domain.model.ServiceType
import com.marlon.portalusuario.presentation.mobileservices.usecases.RefreshAuthToken
import com.marlon.portalusuario.util.Utils.isTokenExpired
import io.github.suitetecsa.sdk.android.balance.FetchBalanceCallBack
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
import io.github.suitetecsa.sdk.exception.InvalidSessionException
import io.github.suitetecsa.sdk.exception.NautaException
import io.github.suitetecsa.sdk.nauta.model.users.UsersRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import com.marlon.portalusuario.data.entity.MobileService as MobServEntity
import com.marlon.portalusuario.domain.model.MobileService as MobServDomain

private const val TAG = "UserRepositoryImpl"

class UserRepositoryImpl(
    private val apiDataSource: UserApiDataSource,
    private val dao: ServicesDao,
    private val refreshAuthToken: RefreshAuthToken,
    private val sessionStorage: SessionStorage,
    private val mobServMapper: MobServMapper,
) : UserRepository {

    private suspend fun fetchUserFromRemote(session: DataSession, refreshToken: Boolean = false) {
        Log.d(TAG, "fetchUser: Fetching user from remote")
        var lastUpdate: String? = null
        val token = session.authToken.takeIf { !it.isTokenExpired() && !refreshToken }
            ?: refreshAuthToken(
                session.phoneNumber,
                session.password,
                session.captchaCode,
                session.idRequest
            )
                .also {
                    lastUpdate = it.second
                    sessionStorage.updateDataSession(
                        session.copy(
                            authToken = it.first,
                            lastUpdate = it.second,
                            updatedServices = it.third
                        )
                    )
                }.first
        runCatching {
            apiDataSource.getUser(
                token,
                UsersRequest(session.portalUser, lastUpdate ?: session.lastUpdate)
            )
                .also { response ->
                    lastUpdate = response.lastUpdate
                    dao.insertClientProfile(with(ClientProfileMapper()) { response.toEntity() })
                    response.services.mobileServices
                        .forEach { dao.insertMobileServices(with(mobServMapper) { it.toEntity() }) }
                    response.services.navServices
                        .forEach { dao.insertNavigationServices(with(NavServMapper()) { it.toEntity() }) }
                }
        }.onSuccess {
            sessionStorage.updateDataSession(
                session.copy(lastUpdate = lastUpdate!!, updatedServices = true)
            )
        }.onFailure {
            it.printStackTrace()
            if (it is InvalidSessionException) fetchUserFromRemote(session, true)
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun fetchUserFromLocal(simCard: SimCard, isRemote: Boolean = false) {
        Log.d(TAG, "fetchUserFromLocal: Fetching user from local using sim card")
        simCard.smartFetchBalance(object : FetchBalanceCallBack {
            var requests: List<UssdRequest> = emptyList()
            var mobService = MobServEntity(
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
                if (isRemote) ServiceType.LocalAndRemote else ServiceType.Local
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
                // Not yet implemented
            }

            override fun onFetching(request: UssdRequest) {
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
                        requests = response.consults
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
                                data.data?.let { addToBonusList(it, "DATOS", data.expires) }
                                data.dataLte?.let { addToBonusList(it, "DATOS LTE", data.expires) }
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

                        runBlocking { dao.insertMobileServices(mobService) }
                    }

                    UssdRequest.CUSTOM -> {}
                }
            }
        })
    }

    override suspend fun fetchUser(simCard: SimCard?) {
        simCard?.also { sim ->
            when (getMobileServices().first().first { it.id == "53${sim.phoneNumber!!}" }.type) {
                ServiceType.Local -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) fetchUserFromLocal(sim)
                ServiceType.Remote -> fetchUserFromRemote(sessionStorage.dataSession.first()!!)
                ServiceType.LocalAndRemote -> {
                    runCatching { fetchUserFromRemote(sessionStorage.dataSession.first()!!) }
                        .onFailure {
                            if (hasReachedMaxAttempts(it) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                fetchUserFromLocal(sim, true)
                            } else {
                                throw it
                            }
                        }
                }
            }
        } ?: run { fetchUserFromRemote(sessionStorage.dataSession.first()!!) }
    }

    private fun hasReachedMaxAttempts(throwable: Throwable) = throwable is NautaException &&
        throwable.message?.endsWith("Máximo número de intentos alcanzado sin respuesta") == true

    override fun getClientProfile(): Flow<ClientProfile> =
        dao.getClientProfile().map { it.asModel() }

    override fun getMobileServices(): Flow<List<MobServDomain>> =
        dao.getMobileServices().map { services ->
            services.map { with(mobServMapper) { it.toDomain() } }
        }

    override fun getNavServices(): Flow<List<NavigationService>> =
        dao.getNavigationServices().map { services ->
            services.map { with(NavServMapper()) { it.toDomain() } }
        }
}
