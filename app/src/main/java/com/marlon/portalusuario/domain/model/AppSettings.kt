package com.marlon.portalusuario.domain.model

data class AppSettings(
    val modeNight: ModeNight = ModeNight.FOLLOW_SYSTEM,
    val isShowingTrafficBubble: Boolean = false,
    val isShowingAccountBalanceOnTrafficBubble: Boolean = false,
    val isShowingDataBalanceOnTrafficBubble: Boolean = false,
    val isIntroOpened: Boolean = false,
)
