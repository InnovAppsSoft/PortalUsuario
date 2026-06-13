package com.marlon.portalusuario.feature.mobileservices.presentation.components.configsimcards

class ConfigSimCardsViewState(val viewModel: ConfigSimCardsViewModel) {
    fun onNext(onFinish: () -> Unit) {
        viewModel.onEvent(ConfigSimCardsEvent.OnSimCardAdd(onFinish))
    }
}
