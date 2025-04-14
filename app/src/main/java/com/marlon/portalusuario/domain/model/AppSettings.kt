package com.marlon.portalusuario.domain.model

data class AppSettings(
    val skipLogin: Boolean = false,
    val modeNight: ModeNight = ModeNight.FOLLOW_SYSTEM,
    val isShowingTrafficBubble: Boolean = false,
    val isIntroOpened: Boolean = false,
)
