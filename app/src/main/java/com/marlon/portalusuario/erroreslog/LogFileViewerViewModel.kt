package com.marlon.portalusuario.erroreslog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

data class LogFileViewerUiState(
    val logs: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

@HiltViewModel
class LogFileViewerViewModel
    @Inject
    constructor() : ViewModel() {
        private val _uiState = MutableStateFlow(LogFileViewerUiState())
        val uiState: StateFlow<LogFileViewerUiState> = _uiState.asStateFlow()

        init {
            loadLogs()
        }

        fun refresh() {
            loadLogs()
        }

        private fun loadLogs() {
            viewModelScope.launch(Dispatchers.IO) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                val file = File(JCLogging.getDirectory(), "log.txt")
                if (!file.exists()) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "No existe archivo de registro")
                    }
                    return@launch
                }
                try {
                    val logs = JCLogging.readFromFile(file)
                    if (logs.isEmpty()) {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "El archivo de registro está vacío")
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, logs = logs) }
                    }
                } catch (ex: IOException) {
                    JCLogging.error(null, null, ex)
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "No existe archivo de registro")
                    }
                }
            }
        }

        fun clearLog() {
            viewModelScope.launch(Dispatchers.IO) {
                JCLogging.clearLog()
                loadLogs()
            }
        }
    }
