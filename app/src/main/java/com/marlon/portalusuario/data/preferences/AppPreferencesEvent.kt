package com.marlon.portalusuario.data.preferences

import com.marlon.portalusuario.domain.model.ModeNight

sealed class AppPreferencesEvent {
    data class OnUpdateSkippedLogin(val value: Boolean) : AppPreferencesEvent()
    data class OnUpdateModeNight(val value: ModeNight) : AppPreferencesEvent()
    data class OnUpdateIsShowingTrafficBubble(val value: Boolean) : AppPreferencesEvent()
    data class OnUpdateIsIntroOpened(val value: Boolean) : AppPreferencesEvent()
    data class OnSwitchingAccountBalanceOnTrafficBubbleVisibility(val value: Boolean) : AppPreferencesEvent()
    data class OnSwitchingDataBalanceOnTrafficBubbleVisibility(val value: Boolean) : AppPreferencesEvent()
}
