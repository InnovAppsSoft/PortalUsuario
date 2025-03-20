package com.marlon.portalusuario.data.user

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.marlon.portalusuario.data.ServicesDao
import com.marlon.portalusuario.data.entity.asModel
import com.marlon.portalusuario.data.mappers.ClientProfileMapper
import com.marlon.portalusuario.data.mappers.MobServMapper
import com.marlon.portalusuario.data.mappers.NavServMapper
import com.marlon.portalusuario.data.source.UserApiDataSource
import com.marlon.portalusuario.data.source.UserLocalDataSource
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.domain.model.ClientProfile
import com.marlon.portalusuario.domain.model.NavigationService
import com.marlon.portalusuario.domain.model.ServiceType.Local
import com.marlon.portalusuario.domain.model.ServiceType.LocalAndRemote
import com.marlon.portalusuario.domain.model.ServiceType.Remote
import io.github.suitetecsa.sdk.android.model.SimCard
import io.github.suitetecsa.sdk.exception.NautaException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.marlon.portalusuario.domain.model.MobileService as MobServDomain

class UserRepositoryImpl(
    private val apiDataSource: UserApiDataSource,
    private val dao: ServicesDao,
    private val mobServMapper: MobServMapper,
    private val localDataSource: UserLocalDataSource = UserLocalDataSource()
) : UserRepository {

    private fun hasReachedMaxAttempts(throwable: Throwable) = throwable is NautaException &&
        throwable.message?.endsWith("Máximo número de intentos alcanzado sin respuesta") == true

    private suspend fun fetchUserFromRemote() = apiDataSource.fetch()
        .let { response ->
            dao.insertClientProfile(with(ClientProfileMapper()) { response.toEntity() })
            response.services.mobileServices
                .forEach { dao.insertMobileServices(with(mobServMapper) { it.toEntity() }) }
            response.services.navServices
                .forEach { dao.insertNavigationServices(with(NavServMapper()) { it.toEntity() }) }
        }

    @SuppressLint("MissingPermission")
    private suspend fun fetchUserFromLocal(simCard: SimCard, isRemote: Boolean = false) =
        localDataSource.fetch(simCard, isRemote).let { dao.insertMobileServices(it) }

    override suspend fun fetchUser(simCard: SimCard?) {
        simCard?.also { sim ->
            when (getMobileServices().first().firstOrNull { it.id == "53${sim.phoneNumber!!}" }?.type ?: Local) {
                Local -> fetchUserFromLocal(sim)
                Remote -> fetchUserFromRemote()
                LocalAndRemote -> {
                    runCatching { fetchUserFromRemote() }
                        .onFailure {
                            if (hasReachedMaxAttempts(it)) {
                                fetchUserFromLocal(sim, true)
                            } else {
                                throw it
                            }
                        }
                }
            }
        } ?: run { fetchUserFromRemote() }
    }

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
