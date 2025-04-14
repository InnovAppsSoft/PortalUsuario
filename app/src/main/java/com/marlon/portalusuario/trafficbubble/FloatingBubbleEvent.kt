package com.marlon.portalusuario.trafficbubble

sealed class FloatingBubbleEvent {
    data object OnCalculateDataUsage : FloatingBubbleEvent()
}
