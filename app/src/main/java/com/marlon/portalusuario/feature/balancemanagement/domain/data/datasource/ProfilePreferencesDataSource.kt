package com.marlon.portalusuario.feature.balancemanagement.domain.data.datasource

import com.marlon.portalusuario.feature.balancemanagement.domain.model.PreferencesProfile
import kotlinx.coroutines.flow.Flow

interface ProfilePreferencesDataSource {
    fun profilePreferences(): Flow<PreferencesProfile>
    suspend fun updateName(name: String)
    suspend fun updatePhoneNumber(phoneNumber: String)
    suspend fun updateNautaMail(mail: String)
}
