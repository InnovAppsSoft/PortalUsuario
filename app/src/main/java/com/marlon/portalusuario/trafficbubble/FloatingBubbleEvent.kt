package com.marlon.portalusuario.trafficbubble

sealed class FloatingBubbleEvent {
    data object OnCalculateDataUsage : FloatingBubbleEvent()
    data class OnSwitchingAccountBalanceVisibility(val isVisible: Boolean) : FloatingBubbleEvent()
    data class OnSwitchingDataBalanceVisibility(val isVisible: Boolean) : FloatingBubbleEvent()
}
