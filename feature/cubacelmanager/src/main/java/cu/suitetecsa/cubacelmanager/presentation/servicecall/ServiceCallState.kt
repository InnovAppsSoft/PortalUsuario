package cu.suitetecsa.cubacelmanager.presentation.servicecall

import cu.suitetecsa.sdk.android.model.SimCard

data class ServiceCallState(
    val simCards: List<SimCard> = listOf(),
    val currentSimCard: SimCard? = null,
)
