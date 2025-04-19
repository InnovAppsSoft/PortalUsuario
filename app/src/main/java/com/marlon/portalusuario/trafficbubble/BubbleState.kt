package com.marlon.portalusuario.trafficbubble

/**
 * Represents the state of the network information displayed in a "bubble" or overlay.
 *
 * This data class encapsulates various network-related metrics and display preferences.
 *
 * @property uploadSpeed The current upload speed in bytes per second. Defaults to 0.
 * @property downloadSpeed The current download speed in bytes per second. Defaults to 0.
 * @property accountBalance The current account balance as a string, including the currency (e.g., "0.00 CUP"). Defaults to "0.00 CUP".
 * @property dataBalance The current data balance as a string, including the unit (e.g., "0 B"). Defaults to "0 B".
 * @property lastTime The timestamp (in milliseconds) of the last network data update. Defaults to 0.
 * @property lastRxBytes The total received bytes at the last update. Defaults to 0.
 * @property lastTxBytes The total transmitted bytes at the last update. Defaults to 0.
 * @property isShowingAccountBalance A boolean indicating whether the account balance should be displayed. Defaults to false.
 * @property isShowingDataBalance A boolean indicating whether the data balance should be displayed. Defaults to false.
 * @property isShowingSpeed A boolean indicating whether the upload and download speeds should be displayed. Defaults to false.
 */
data class BubbleState(
    var uploadSpeed: Long = 0,
    var downloadSpeed: Long = 0,
    var accountBalance: String = "0.00 CUP",
    var dataBalance: String = "0 B",
    val lastTime: Long = 0,
    val lastRxBytes: Long = 0,
    val lastTxBytes: Long = 0,
    val isShowingAccountBalance: Boolean = false,
    val isShowingDataBalance: Boolean = false,
    val isShowingSpeed: Boolean = false,
)
