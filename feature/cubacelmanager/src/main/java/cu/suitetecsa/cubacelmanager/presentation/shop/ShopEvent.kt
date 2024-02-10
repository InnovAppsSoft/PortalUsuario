package cu.suitetecsa.cubacelmanager.presentation.shop

import cu.suitetecsa.sdk.android.model.SimCard

sealed class ShopEvent {
    data class ChangeSimCard(val simCard: SimCard) : ShopEvent()
    data class Buy(val ussdCode: String, val onChangeState: (Boolean) -> Unit) : ShopEvent()
}
