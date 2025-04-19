package com.marlon.portalusuario.trafficbubble

import android.net.TrafficStats
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.data.preferences.AppPreferencesManager
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.trafficbubble.Util.calcDownSpeed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val BYTE_UNIT_SI = 1000

@HiltViewModel
class FloatingBubbleViewModel @Inject constructor(
    private val appSettings: AppPreferencesManager,
    private val mobileServicesRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(BubbleState())
    val state: StateFlow<BubbleState> get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            appSettings.preferences().first().let { settings ->
                _state.value = state.value.copy(
                    isShowingAccountBalance = settings.isShowingAccountBalanceOnTrafficBubble,
                    isShowingDataBalance = settings.isShowingDataBalanceOnTrafficBubble
                )
            }
            mobileServicesRepository.getMobileServices().collect { services ->
                services.takeIf { it.isNotEmpty() }?.first()?.let { service ->
                    val dataAvailable = if (service.plans.firstOrNull { it.type == "DATOS" } != null && service.plans.firstOrNull { it.type == "DATOS LTE" } != null) {
                        DataAvailable.DATA_AND_DATA_LTE
                    } else if (service.plans.firstOrNull { it.type == "DATOS" } != null) {
                        DataAvailable.DATA
                    } else if (service.plans.firstOrNull { it.type == "DATOS LTE" } != null) {
                        DataAvailable.DATA_LTE
                    } else null

                    if (_state.value.isShowingAccountBalance) _state.value = _state.value.copy(accountBalance = "${service.mainBalance} CUP")
                    if (_state.value.isShowingDataBalance) _state.value = _state.value.copy(
                        dataBalance = when (dataAvailable) {
                            DataAvailable.DATA -> service.plans.first { it.type == "DATOS" }.data
                            DataAvailable.DATA_LTE -> "${service.plans.first { it.type == "DATOS LTE" }.data} LTE"
                            DataAvailable.DATA_AND_DATA_LTE -> "${service.plans.first { it.type == "DATOS" }.data} + ${service.plans.first { it.type == "DATOS LTE" }.data} LTE"
                            null -> "0 B"
                        }
                    )
                }
            }
        }
    }

    /**
     * Handles various events related to the floating bubble UI.
     *
     * This function processes different `FloatingBubbleEvent` types, updating the UI state accordingly.
     *
     * @param event The `FloatingBubbleEvent` to be processed. It can be one of the following:
     *              - `FloatingBubbleEvent.OnCalculateDataUsage`: Triggers the calculation of data usage.
     *              - `FloatingBubbleEvent.OnSwitchingAccountBalanceVisibility`: Controls the visibility of the account balance.
     *              - `FloatingBubbleEvent.OnSwitchingDataBalanceVisibility`: Controls the visibility of the data balance.
     *
     * @see FloatingBubbleEvent
     * @see BubbleState
     */
    fun onEvent(event: FloatingBubbleEvent) {
        when(event) {
            FloatingBubbleEvent.OnCalculateDataUsage -> calculateDataUsage()
            is FloatingBubbleEvent.OnSwitchingAccountBalanceVisibility ->
                _state.value = _state.value.copy(isShowingAccountBalance = event.isVisible)
            is FloatingBubbleEvent.OnSwitchingDataBalanceVisibility ->
                _state.value = _state.value.copy(isShowingDataBalance = event.isVisible)
        }
    }

    /**
     * Calculates the current data usage (upload and download speed) and updates the state accordingly.
     *
     * This function does the following:
     * 1. Gets the current timestamp.
     * 2. Calculates the time elapsed since the last data usage check (`usedTime`).
     * 3. Fetches the current total received bytes (`currentRxBytes`) and transmitted bytes (`currentTxBytes`) using `TrafficStats`.
     * 4. Calculates the difference between the current received/transmitted bytes and the last recorded received/transmitted bytes (`usedRxBytes`, `usedTxBytes`). This represents the data used since the last check.
     * 5. Updates the internal state (`_state`) with the following:
     *    - `lastTime`: The current timestamp.
     *    - `lastRxBytes`: The current total received bytes.
     *    - `lastTxBytes`: The current total transmitted bytes.
     *    - `uploadSpeed`: The calculated upload speed (in bytes per millisecond) using `calcUpSpeed()`.
     *    - `downloadSpeed`: The calculated download speed (in bytes per millisecond) using `calcDownSpeed()`.
     *
     * Note: It relies on the existence of `_state` (presumably a mutable state holder), and helper functions `calcUpSpeed()` and `calcDownSpeed()` for speed calculations. Also the `_state.value` has the property `lastTime`, `lastRxBytes` and `lastTxBytes`
     *
     * The function assumes that `TrafficStats.getTotalRxBytes()` and `TrafficStats.getTotalTxBytes()` return a valid byte count,
     * and that negative values will not lead to incorrect behavior.
     *
     * The `calcUpSpeed` and `calcDownSpeed` functions are not provided in this context and should calculate data upload and download speed respectively.
     *
     * @see TrafficStats
     * @see calcUpSpeed
     * @see calcDownSpeed
     */
    private fun calculateDataUsage() {
        val currentTime = System.currentTimeMillis()
        val usedTime = currentTime - _state.value.lastTime
        val currentRxBytes = TrafficStats.getTotalRxBytes()
        val currentTxBytes = TrafficStats.getTotalTxBytes()
        val usedRxBytes = currentRxBytes - _state.value.lastRxBytes
        val usedTxBytes = currentTxBytes - _state.value.lastTxBytes

        _state.value = _state.value.copy(
            lastTime = currentTime,
            lastRxBytes = currentRxBytes,
            lastTxBytes = currentTxBytes,
            uploadSpeed = calcUpSpeed(usedTime, usedTxBytes),
            downloadSpeed = calcDownSpeed(usedTime, usedRxBytes),
        )
    }

    /**
     * Calculates the upload speed in bytes per second.
     *
     * This function determines the upload speed based on the number of bytes uploaded
     * and the time taken to upload them. It handles cases where the time taken is zero
     * to prevent division by zero errors.
     *
     * @param timeTaken The time taken for the upload in milliseconds.
     * @param upBytes The number of bytes uploaded during that time.
     * @return The upload speed in bytes per second. Returns 0 if `timeTaken` is 0 or negative.
     *
     * @sample
     * val speed1 = calcUpSpeed(1000, 1024) // Returns 1024
     * val speed2 = calcUpSpeed(500, 2048) // Returns 4096
     * val speed3 = calcUpSpeed(0, 5000) // Returns 0
     * val speed4 = calcUpSpeed(-1, 5000) //Returns 0
     */
    private fun calcUpSpeed(timeTaken: Long, upBytes: Long): Long =
        if (timeTaken > 0) upBytes * BYTE_UNIT_SI / timeTaken else 0
}

private enum class DataAvailable {
    DATA, DATA_LTE, DATA_AND_DATA_LTE
}