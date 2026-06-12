package com.marlon.portalusuario.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.database.une.UneRepository
import com.marlon.portalusuario.une.TarifaElect
import com.marlon.portalusuario.une.Une
import com.marlon.portalusuario.util.Util
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
        application: Application,
    ) : AndroidViewModel(application) {
        private val uneRepository = UneRepository(application)

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
            val previousText = state.previousReading.trim()
            val currentText = state.currentReading.trim()

            val errors = mutableListOf<String>()
            val previous = previousText.toDoubleOrNull()
            if (previous == null) errors.add("Lectura Anterior: Valor inválido")

            val current = currentText.toDoubleOrNull()
            if (current == null) errors.add("Lectura Actual: Valor inválido")

            if (errors.isNotEmpty()) {
                _uiState.update { it.copy(errorMessage = errors.joinToString("\n")) }
                return
            }

            if (current!! < 0) {
                _uiState.update { it.copy(errorMessage = "Lectura Actual: Valor inválido") }
                return
            }
            if (previous!! < 0) {
                _uiState.update { it.copy(errorMessage = "Lectura Anterior: Valor inválido") }
                return
            }
            if (current < previous) {
                _uiState.update {
                    it.copy(
                        errorMessage = "La lectura Anterior no puede ser mayor a la Actual",
                    )
                }
                return
            }
            if (current == previous) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Lectura anterior y Lectura Actual no deben ser iguales",
                    )
                }
                return
            }

            val consumption = Util.roundDouble(current - previous)
            val totalToPay = Util.roundDouble(TarifaElect.calculateCost(consumption))

            _uiState.update {
                it.copy(
                    consumption = consumption,
                    totalToPay = totalToPay,
                    errorMessage = null,
                )
            }

            insertUne(
                Une(
                    date = Util.currentDate2Long(),
                    lastRegister = previous,
                    currentRegister = current,
                    totalConsumption = consumption,
                    totalToPay = totalToPay,
                ),
            )
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
