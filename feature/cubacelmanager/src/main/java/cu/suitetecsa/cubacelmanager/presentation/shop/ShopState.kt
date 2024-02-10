package cu.suitetecsa.cubacelmanager.presentation.shop

import cu.suitetecsa.sdk.android.model.SimCard

data class ShopState(
    val simCards: List<SimCard> = listOf(),
    val currentSimCard: SimCard? = null,
    val canRun: Boolean = false,
    val loading: Boolean = false,
    val runningMessage: String? = null,
)
