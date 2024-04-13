package com.marlon.portalusuario.trafficbubble

data class BubbleState(
    var uploadSpeed: Long = 0L,
    var downloadSpeed: Long = 0L,
    var accountBalance: String = "0.00 CUP",
    var dataBalance: String = "0 B"
)
