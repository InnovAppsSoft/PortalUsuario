package com.marlon.portalusuario.presentation.mobileservices.components.configsimcards

class ConfigSimCardsViewState(val viewModel: ConfigSimCardsViewModel) {
    fun onNext(onFinish: () -> Unit) {
        viewModel.onEvent(ConfigSimCardsEvent.OnSimCardAdd(onFinish))
    }
}
