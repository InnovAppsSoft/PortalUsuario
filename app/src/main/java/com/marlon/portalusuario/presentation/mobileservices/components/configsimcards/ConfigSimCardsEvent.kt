package com.marlon.portalusuario.presentation.mobileservices.components.configsimcards

sealed class ConfigSimCardsEvent {
    data object OnNext : ConfigSimCardsEvent()

    data class OnSimCardAdd(val onFinish: () -> Unit) : ConfigSimCardsEvent()
    data class OnChangedPhoneNumber(val value: String) : ConfigSimCardsEvent()
}
