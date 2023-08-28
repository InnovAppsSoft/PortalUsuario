package com.marlon.portalusuario.feature.balancemanagement.view

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.feature.balancemanagement.SendUssdRequestUseCase
import com.marlon.portalusuario.feature.balancemanagement.data.repository.BalancePreferencesRepository
import com.marlon.portalusuario.feature.balancemanagement.data.repository.SimCardsRepository
import cu.suitetecsa.sdk.sim.model.SimCard
import cu.suitetecsa.sdk.ussd.model.BonusBalance
import cu.suitetecsa.sdk.ussd.model.MainBalance
import cu.suitetecsa.sdk.ussd.uitls.parseBonusBalance
import cu.suitetecsa.sdk.ussd.uitls.parseDailyData
import cu.suitetecsa.sdk.ussd.uitls.parseMailData
import cu.suitetecsa.sdk.ussd.uitls.parseMainBalance
import cu.suitetecsa.sdk.ussd.uitls.parseMainData
import cu.suitetecsa.sdk.ussd.uitls.parseMainSms
import cu.suitetecsa.sdk.ussd.uitls.parseMainVoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DELAY_TIME = 3000L

@HiltViewModel
class BalanceManagementViewModel @Inject constructor(
    simCardsRepository: SimCardsRepository,
    private val balancePreferencesRepository: BalancePreferencesRepository,
    private val sendUssdRequestUseCase: SendUssdRequestUseCase
) : ViewModel() {

    private val _simCards = MutableLiveData<List<SimCard>>(listOf())
    val simCards: LiveData<List<SimCard>> get() = _simCards
    private val _currentSimCard = MutableLiveData<SimCard?>(null)
    val currentSimCard: LiveData<SimCard?> get() = _currentSimCard

    private val balancePreferences = balancePreferencesRepository.getBalancePreferences().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null
    )

    private val _mainBalance: MutableLiveData<MainBalance?> = MutableLiveData(null)

    val mainBalance: LiveData<MainBalance?> get() = _mainBalance
    private val _bonusBalance: MutableLiveData<BonusBalance?> = MutableLiveData(null)

    val bonusBalance: LiveData<BonusBalance?> get() = _bonusBalance

    init {
        _simCards.value = simCardsRepository.getSimCards()
        viewModelScope.launch {
            balancePreferences.collect { preferences ->
                preferences?.let {
                    if (it.currentSimCardId.isNotEmpty()) {
                        _currentSimCard.value = simCardsRepository.getSimCards().first { simCard ->
                            simCard.serialNumber == it.currentSimCardId
                        }
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
    fun getBalances(notifyActionRunning: (String) -> Unit) {
        viewModelScope.launch {
            notifyActionRunning("Consultando saldo...")
            val main = sendUssdRequestUseCase("*222#").parseMainBalance()
            _mainBalance.value = main

            if (main.data.data != null || main.data.dataLte != null) {
                delay(DELAY_TIME)
                notifyActionRunning("Consultando datos...")
                val dataResponse = sendUssdRequestUseCase("*222*328#")
                _mainBalance.value!!.data = dataResponse.parseMainData()
                _mainBalance.value!!.dailyData = dataResponse.parseDailyData()
                _mainBalance.value!!.mailData = dataResponse.parseMailData()
            }

            main.voice.mainVoice?.let {
                delay(DELAY_TIME)
                notifyActionRunning("Consultando minutos...")
                _mainBalance.value!!.voice = sendUssdRequestUseCase("*222*869#").parseMainVoice()
            }

            main.sms.mainSms?.let {
                delay(DELAY_TIME)
                notifyActionRunning("Consultando sms...")
                _mainBalance.value!!.sms = sendUssdRequestUseCase("*222*767#").parseMainSms()
            }

            delay(DELAY_TIME)
            notifyActionRunning("Consultando bonos...")
            _bonusBalance.value = sendUssdRequestUseCase("*222*266#").parseBonusBalance()
        }
    }
}