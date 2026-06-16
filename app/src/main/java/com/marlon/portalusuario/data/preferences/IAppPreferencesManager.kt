package com.marlon.portalusuario.data.preferences

import com.marlon.portalusuario.domain.model.AppSettings
import com.marlon.portalusuario.domain.model.ModeNight
import kotlinx.coroutines.flow.Flow

interface IAppPreferencesManager {
    fun preferences(): Flow<AppSettings>

    suspend fun updateIsShowingAccountBalanceOnTrafficBubble(value: Boolean)

    suspend fun updateIsShowingDataBalanceOnTrafficBubble(value: Boolean)

    suspend fun updateModeNight(modeNight: ModeNight)

    suspend fun updateIsDynamicColor(isDynamicColor: Boolean)

    suspend fun updateIsShowingTrafficBubble(value: Boolean)

    suspend fun updateIsIntroOpened(value: Boolean)
}
