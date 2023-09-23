package cu.suitetecsa.nautanav.ui.components.connectview

sealed class ConnectViewState {
    data class Connected(val remainingTime: String, val onLogout: () -> Unit) :
        ConnectViewState()

    data class Disconnected(
        val remainingTime: String,
        val onSelectLimitedTime: () -> Unit,
        val onLogin: () -> Unit
    ) : ConnectViewState()

    data class Loading(val remainingTime: String) : ConnectViewState()
    data class Failure(
        val errorMessage: String,
        val actionLabel: String,
        val onAction: () -> Unit
    ) : ConnectViewState()
}