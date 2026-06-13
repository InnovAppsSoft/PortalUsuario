package com.marlon.portalusuario.testhelpers

import com.marlon.portalusuario.data.preferences.IAppPreferencesManager
import com.marlon.portalusuario.domain.model.AppSettings
import com.marlon.portalusuario.domain.model.ModeNight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAppPreferencesManager(
    initialSettings: AppSettings = AppSettings(),
) : IAppPreferencesManager {
    private val _settings = MutableStateFlow(initialSettings)

    override fun preferences(): Flow<AppSettings> = _settings

    override suspend fun updateIsShowingAccountBalanceOnTrafficBubble(value: Boolean) {
        _settings.value = _settings.value.copy(isShowingAccountBalanceOnTrafficBubble = value)
    }

    override suspend fun updateIsShowingDataBalanceOnTrafficBubble(value: Boolean) {
        _settings.value = _settings.value.copy(isShowingDataBalanceOnTrafficBubble = value)
    }

    override suspend fun updateModeNight(modeNight: ModeNight) {
        _settings.value = _settings.value.copy(modeNight = modeNight)
    }

    override suspend fun updateIsDynamicColor(isDynamicColor: Boolean) {
        _settings.value = _settings.value.copy(isDynamicColor = isDynamicColor)
    }

    override suspend fun updateIsShowingTrafficBubble(value: Boolean) {
        _settings.value = _settings.value.copy(isShowingTrafficBubble = value)
    }

    override suspend fun updateIsIntroOpened(value: Boolean) {
        _settings.value = _settings.value.copy(isIntroOpened = value)
    }
}
