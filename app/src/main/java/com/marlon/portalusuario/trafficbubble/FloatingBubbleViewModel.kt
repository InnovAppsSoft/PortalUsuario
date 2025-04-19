package com.marlon.portalusuario.trafficbubble

import android.net.TrafficStats
import android.util.Log
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
private const val TAG = "FloatingBubbleViewModel"

@HiltViewModel
class FloatingBubbleViewModel @Inject constructor(
    private val appSettings: AppPreferencesManager,
    private val mobileServicesRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(BubbleState())
    val state: StateFlow<BubbleState> get() = _state.asStateFlow()

    init {
        collectPreferences()
        collectMobileServices()
    }

    private fun collectPreferences() {
        viewModelScope.launch {
            appSettings.preferences().collect { settings ->
                _state.value = state.value.copy(
                    isShowingAccountBalance = settings.isShowingAccountBalanceOnTrafficBubble,
                    isShowingDataBalance = settings.isShowingDataBalanceOnTrafficBubble
                )
            }
        }
    }

    private fun collectMobileServices() {
        viewModelScope.launch {
            mobileServicesRepository.getMobileServices().collect { services ->
                Log.d(TAG, "init: services count :: ${services.count()}")
                services.takeIf { it.isNotEmpty() }?.first()?.let { service ->
                    val dataAvailable =
                        if (service.plans.firstOrNull { it.type == "DATOS" } != null && service.plans.firstOrNull { it.type == "DATOS LTE" } != null) {
                            DataAvailable.DATA_AND_DATA_LTE
                        } else if (service.plans.firstOrNull { it.type == "DATOS" } != null) {
                            DataAvailable.DATA
                        } else if (service.plans.firstOrNull { it.type == "DATOS LTE" } != null) {
                            DataAvailable.DATA_LTE
                        } else null

                    Log.d(TAG, "init: accountBalance :: ${service.mainBalance} CUP")

                    _state.value = _state.value.copy(
                        accountBalance = "${service.mainBalance} CUP",
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

    private fun calcUpSpeed(timeTaken: Long, upBytes: Long): Long =
        if (timeTaken > 0) upBytes * BYTE_UNIT_SI / timeTaken else 0
}

private enum class DataAvailable {
    DATA, DATA_LTE, DATA_AND_DATA_LTE
}