package com.marlon.portalusuario.feature.balancemanagement.framework.view

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.feature.balancemanagement.data.repository.BalancePreferencesRepository
import com.marlon.portalusuario.feature.balancemanagement.data.repository.BalancesRepository
import com.marlon.portalusuario.feature.profile.domain.data.repository.ProfilePreferencesRepository
import com.marlon.portalusuario.feature.balancemanagement.data.repository.SimCardsRepository
import com.marlon.portalusuario.feature.balancemanagement.usecases.UpdateBalancesUseCase
import cu.suitetecsa.sdk.sim.model.SimCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BalanceManagementViewModel @Inject constructor(
        simCardsRepository: SimCardsRepository,
        balancesRepository: BalancesRepository,
        profilePreferencesRepository: ProfilePreferencesRepository,
        private val balancePreferencesRepository: BalancePreferencesRepository,
        private val updateBalancesUseCase: UpdateBalancesUseCase
) : ViewModel() {

    private val _simCards = MutableStateFlow<List<SimCard>>(listOf())
    val simCards: StateFlow<List<SimCard>> get() = _simCards.asStateFlow()
    private val _currentSimCard = MutableStateFlow<SimCard?>(null)
    val currentSimCard: StateFlow<SimCard?> get() = _currentSimCard.asStateFlow()

    private val balancePreferences = balancePreferencesRepository.getBalancePreferences().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    val profilePreferences = profilePreferencesRepository.getProfilePreferences().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    val mainBalance = balancesRepository.mainBalancesFromCache().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )
    val bonusBalance = balancesRepository.bonusBalancesFromCache().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    init {
        _simCards.value = simCardsRepository.getSimCards()
        viewModelScope.launch {
            balancePreferences.collect { preferences ->
                preferences?.let {
                    if (it.currentSimCardId.isNotEmpty()) {
                        _currentSimCard.value = simCardsRepository.getSimCards().firstOrNull { simCard ->
                            simCard.serialNumber == it.currentSimCardId
                        } ?: simCardsRepository.getSimCards().first()

                    }
                }
            }
        }
    }

    fun changeCurrentSimCard(simCard: SimCard) {
        viewModelScope.launch {
            balancePreferencesRepository.updateCurrentSimCardId(simCard.serialNumber)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(allOf = [Manifest.permission.CALL_PHONE])
    fun updateBalances(notifyActionRunning: (String) -> Unit) {
        viewModelScope.launch {
            _currentSimCard.value?.let { updateBalancesUseCase(it, notifyActionRunning) }
        }
    }
}