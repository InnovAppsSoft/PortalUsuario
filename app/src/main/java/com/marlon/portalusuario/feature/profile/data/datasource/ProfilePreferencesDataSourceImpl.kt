package com.marlon.portalusuario.feature.profile.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.marlon.portalusuario.feature.balancemanagement.domain.model.PreferencesProfile
import com.marlon.portalusuario.feature.profile.domain.data.datasource.ProfilePreferencesDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val PROFILE_PREFERENCES_NAME = "profile_preferences"
private val Context.dataStore by preferencesDataStore(name = PROFILE_PREFERENCES_NAME)

@Singleton
class ProfilePreferencesDataSourceImpl
@Inject constructor(@ApplicationContext private val context: Context) :
        ProfilePreferencesDataSource {
    override fun profilePreferences(): Flow<PreferencesProfile> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val name = preferences[ProfilePreferencesKeys.NAME] ?: ""
            val phoneNumber = preferences[ProfilePreferencesKeys.PHONE_NUMBER] ?: ""
            val nautaMail = preferences[ProfilePreferencesKeys.NAUTA_MAIL] ?: ""

            PreferencesProfile(name, phoneNumber, nautaMail)
        }

    override suspend fun updateName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[ProfilePreferencesKeys.NAME] = name
        }
    }

    override suspend fun updatePhoneNumber(phoneNumber: String) {
        context.dataStore.edit { preferences ->
            preferences[ProfilePreferencesKeys.PHONE_NUMBER] = phoneNumber
        }
    }

    override suspend fun updateNautaMail(mail: String) {
        context.dataStore.edit { preferences ->
            preferences[ProfilePreferencesKeys.NAUTA_MAIL] = mail
        }
    }
}

private object ProfilePreferencesKeys {
    val NAME = stringPreferencesKey("NAME")
    val PHONE_NUMBER = stringPreferencesKey("PHONE_NUMBER")
    val NAUTA_MAIL = stringPreferencesKey("NAUTA_MAIL")
}