package com.marlon.portalusuario.presentation.mobileservices.components.configsimcards

class ConfigSimCardsViewState(val viewModel: ConfigSimCardsViewModel) {
    fun onNext(onFinished: () -> Unit) {
        viewModel.onEvent(ConfigSimCardsEvent.OnSimCardAdd(onFinished))
    }
}
