package com.marlon.portalusuario.domain.data

import com.marlon.portalusuario.domain.model.ClientProfile
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.NavigationService
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun fetchUser()
    fun getClientProfile(): Flow<List<ClientProfile>>
    fun getMobileServices(): Flow<List<MobileService>>
    fun getNavServices(): Flow<List<NavigationService>>
}
