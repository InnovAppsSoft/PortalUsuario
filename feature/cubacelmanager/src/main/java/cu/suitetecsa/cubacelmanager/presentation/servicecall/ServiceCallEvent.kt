package cu.suitetecsa.cubacelmanager.presentation.servicecall

import cu.suitetecsa.sdk.android.model.SimCard

sealed class ServiceCallEvent {
    data class ChangeSimCard(val simCard: SimCard) : ServiceCallEvent()
    data class Call(val number: String) : ServiceCallEvent()
}
