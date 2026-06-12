package com.marlon.portalusuario.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.database.une.UneRepository
import com.marlon.portalusuario.une.Une
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

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

        val allUnesLiveData: LiveData<List<Une>> = allUnes.asLiveData()

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
