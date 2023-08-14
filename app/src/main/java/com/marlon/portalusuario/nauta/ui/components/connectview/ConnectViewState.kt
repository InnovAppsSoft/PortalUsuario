package com.marlon.portalusuario.nauta.ui.components.connectview

sealed class ConnectViewState {
    object Connecting: ConnectViewState()
    object Connected: ConnectViewState()
    object Disconnected: ConnectViewState()
    data class FailConnectStatus(val reason: String): ConnectViewState()
}