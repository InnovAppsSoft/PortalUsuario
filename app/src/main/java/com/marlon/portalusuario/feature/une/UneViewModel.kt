package com.marlon.portalusuario.feature.une

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.domain.data.UneRepository
import com.marlon.portalusuario.feature.une.domain.usecases.CalculateElectricityCostUseCase
import com.marlon.portalusuario.feature.une.domain.usecases.ElectricityCostResult
import com.marlon.portalusuario.feature.une.Une
import com.marlon.portalusuario.core.util.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UneUiState(
    val previousReading: String = "",
    val currentReading: String = "",
    val consumption: Double? = null,
    val totalToPay: Double? = null,
    val errorMessage: String? = null,
    val showBottomSheet: Boolean = false,
)

@HiltViewModel
class UneViewModel
    @Inject
    constructor(
        private val uneRepository: UneRepository,
        private val calculateElectricityCost: CalculateElectricityCostUseCase,
    ) : ViewModel() {
        val allUnes: StateFlow<List<Une>> =
            uneRepository.allUnes
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = emptyList(),
                )

        private val _uiState = MutableStateFlow(UneUiState())
        val uiState: StateFlow<UneUiState> = _uiState.asStateFlow()

        fun updatePreviousReading(value: String) {
            _uiState.update { it.copy(previousReading = value, errorMessage = null) }
        }

        fun updateCurrentReading(value: String) {
            _uiState.update { it.copy(currentReading = value, errorMessage = null) }
        }

        fun calculate() {
            val state = _uiState.value
            when (val result = calculateElectricityCost(state.previousReading, state.currentReading)) {
                is ElectricityCostResult.Success -> {
                    _uiState.update {
                        it.copy(
                            consumption = result.consumption,
                            totalToPay = result.totalToPay,
                            errorMessage = null,
                        )
                    }
                    insertUne(
                        Une(
                            date = Util.currentDate2Long(),
                            lastRegister = state.previousReading.toDoubleOrNull() ?: 0.0,
                            currentRegister = state.currentReading.toDoubleOrNull() ?: 0.0,
                            totalConsumption = result.consumption,
                            totalToPay = result.totalToPay,
                        ),
                    )
                }
                is ElectricityCostResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
            }
        }

        fun clear() {
            _uiState.update {
                UneUiState()
            }
        }

        fun toggleBottomSheet() {
            _uiState.update { it.copy(showBottomSheet = !it.showBottomSheet) }
        }

        fun dismissBottomSheet() {
            _uiState.update { it.copy(showBottomSheet = false) }
        }

        fun insertUne(une: Une) {
            viewModelScope.launch {
                uneRepository.insertUne(une)
            }
        }

        fun updateUne(une: Une) {
            viewModelScope.launch {
                uneRepository.updateUne(une)
            }
        }

        fun deleteUne(une: Une) {
            viewModelScope.launch {
                uneRepository.deleteUne(une)
            }
        }

        fun deleteAllUnes() {
            viewModelScope.launch {
                uneRepository.deleteAllUnes()
            }
        }
    }
