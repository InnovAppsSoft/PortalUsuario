package com.marlon.portalusuario.feature.balancemanagement.data.repository

import com.marlon.portalusuario.feature.balancemanagement.domain.data.datasource.ProfilePreferencesDataSource
import javax.inject.Inject

class ProfilePreferencesRepository @Inject constructor(
    private val dataSource: ProfilePreferencesDataSource
) {
    fun getProfilePreferences() = dataSource.profilePreferences()
    suspend fun updateName(name: String) = dataSource.updateName(name)
    suspend fun updateEmail(email: String) = dataSource.updateNautaMail(email)
    suspend fun updatePhone(phone: String) = dataSource.updatePhoneNumber(phone)
}