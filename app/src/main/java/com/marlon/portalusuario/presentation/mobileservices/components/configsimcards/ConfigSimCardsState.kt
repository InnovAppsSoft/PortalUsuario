package com.marlon.portalusuario.presentation.mobileservices.components.configsimcards

import io.github.suitetecsa.sdk.android.model.SimCard

data class ConfigSimCardsState(
    val currentSimCard: SimCard,
    val simCards: List<SimCard> = emptyList(),
    val isLoading: Boolean = false,
    val phoneNumber: String = ""
)
