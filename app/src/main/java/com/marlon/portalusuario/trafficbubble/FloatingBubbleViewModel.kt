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
            mobileServicesRepository.getMobileServices().collect { services ->
                services.takeIf { it.isNotEmpty() }?.first()?.let { service ->
                    val dataAvailable = if (service.plans.firstOrNull { it.type == "DATOS" } != null && service.plans.firstOrNull { it.type == "DATOS LTE" } != null) {
                        DataAvailable.DATA_AND_DATA_LTE
                    } else if (service.plans.firstOrNull { it.type == "DATOS" } != null) {
                        DataAvailable.DATA
                    } else if (service.plans.firstOrNull { it.type == "DATOS LTE" } != null) {
                        DataAvailable.DATA_LTE
                    } else null

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

    fun onEvent(event: FloatingBubbleEvent) {
        when(event) {
            FloatingBubbleEvent.OnCalculateDataUsage -> calculateDataUsage()
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