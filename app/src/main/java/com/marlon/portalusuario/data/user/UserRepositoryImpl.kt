package com.marlon.portalusuario.data.user

import android.annotation.SuppressLint
import com.marlon.portalusuario.data.ServicesDao
import com.marlon.portalusuario.data.mappers.ClientProfileApiToEntityMapper
import com.marlon.portalusuario.data.mappers.ClientProfileEntityToDomainMapper
import com.marlon.portalusuario.data.mappers.MobServiceApiToEntityMapper
import com.marlon.portalusuario.data.mappers.MobServiceEntityToDomainMapper
import com.marlon.portalusuario.data.mappers.NavServApiToEntityMapper
import com.marlon.portalusuario.data.mappers.NavServEntityToDomainMapper
import com.marlon.portalusuario.data.source.UserApiDataSource
import com.marlon.portalusuario.data.source.UserLocalDataSource
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.domain.model.ClientProfile
import com.marlon.portalusuario.domain.model.NavigationService
import com.marlon.portalusuario.domain.model.ServiceType.Local
import com.marlon.portalusuario.domain.model.ServiceType.LocalAndRemote
import com.marlon.portalusuario.domain.model.ServiceType.Remote
import com.marlon.portalusuario.util.Utils.parseLastUpdated
import io.github.suitetecsa.sdk.android.model.SimCard
import io.github.suitetecsa.sdk.exception.NautaException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.marlon.portalusuario.domain.model.MobileService as MobServDomain

class UserRepositoryImpl(
    private val apiDataSource: UserApiDataSource,
    private val dao: ServicesDao,
    private val mobServiceApiToEntityMapper: MobServiceApiToEntityMapper,
    private val mobServiceEntityToDomainMapper: MobServiceEntityToDomainMapper,
    private val clientProfileApiToEntityMapper: ClientProfileApiToEntityMapper,
    private val clientProfileEntityToDomainMapper: ClientProfileEntityToDomainMapper,
    private val navServApiToEntityMapper: NavServApiToEntityMapper,
    private val navServEntityToDomainMapper: NavServEntityToDomainMapper,
    private val localDataSource: UserLocalDataSource = UserLocalDataSource()
) : UserRepository {

    private fun hasReachedMaxAttempts(throwable: Throwable) = throwable is NautaException &&
            throwable.message?.endsWith("Máximo número de intentos alcanzado sin respuesta") == true

    private suspend fun fetchUserFromRemote() = apiDataSource.fetch()
        .let { response ->
            dao.insertClientProfile(clientProfileApiToEntityMapper.map(response))
            response.services.mobileServices
                .map(mobServiceApiToEntityMapper::map)
                .forEach { dao.insertMobileServices(it) }
            response.services.navServices
                .map(navServApiToEntityMapper::map)
                .forEach { dao.insertNavigationServices(it) }
        }

    @SuppressLint("MissingPermission")
    private suspend fun fetchUserFromLocal(simCard: SimCard, isRemote: Boolean = false) =
        localDataSource.fetch(simCard, isRemote).let { service ->
            val lastUpdated = System.currentTimeMillis()
            dao.insertMobileServices(service.copy(lastUpdated = lastUpdated))
        }

    override suspend fun fetchUser(simCard: SimCard?) {
        simCard?.also { sim ->
            when (getMobileServices().first()
                .firstOrNull { it.id == "53${sim.phoneNumber!!}" }?.type ?: Local) {
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
        dao.getClientProfile().map(clientProfileEntityToDomainMapper::map)

    override fun getMobileServices(): Flow<List<MobServDomain>> =
        dao.getMobileServices().map { services ->
            services.map(mobServiceEntityToDomainMapper::map)
        }

    override fun getNavServices(): Flow<List<NavigationService>> =
        dao.getNavigationServices().map { services ->
            services.map(navServEntityToDomainMapper::map)
        }
}
