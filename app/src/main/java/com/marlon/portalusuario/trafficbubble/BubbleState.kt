package com.marlon.portalusuario.trafficbubble

data class BubbleState(
    var uploadSpeed: Long = 0,
    var downloadSpeed: Long = 0,
    var accountBalance: String = "0.00 CUP",
    var dataBalance: String = "0 B",
    val lastTime: Long = 0,
    val lastRxBytes: Long = 0,
    val lastTxBytes: Long = 0,
)
