package com.marlon.portalusuario.presentation.mobileservices.components.configsimcards

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.data.preferences.MobServicesPreferences
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.domain.model.MobServPreferences
import com.marlon.portalusuario.domain.model.SlotIndexInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.suitetecsa.sdk.android.SimCardCollector
import io.github.suitetecsa.sdk.android.model.SimCard
import io.github.suitetecsa.sdk.android.utils.extractShortNumber
import io.github.suitetecsa.sdk.android.utils.validateFormat
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val TAG = "ConfigSimCardsViewModel"

@HiltViewModel
class ConfigSimCardsViewModel @Inject constructor(
    private val preferences: MobServicesPreferences,
    private val repository: UserRepository,
    private val simCardCollector: SimCardCollector
) : ViewModel() {
    private val simCards: List<SimCard>
        @SuppressLint("MissingPermission")
        get() = runBlocking {
            preferences.preferences.first().slotIndexInfoList.let { indexes ->
                simCardCollector.collect().filter {
                    it.slotIndex !in indexes.map { index -> index.index }
                }
            }
        }

    private val pref = preferences.preferences.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        MobServPreferences(emptyList())
    )

    private val _state = mutableStateOf(
        ConfigSimCardsState(
            simCards.first(),
            simCards = simCards,
            phoneNumber = simCards.first().phoneNumber?.let { extractShortNumber(it) } ?: ""
        )
    )
    val state get() = _state.value

    val isPhoneNumberValid: Boolean
        get() = validateFormat(_state.value.phoneNumber)?.length == 8

    fun onEvent(event: ConfigSimCardsEvent) {
        when (event) {
            is ConfigSimCardsEvent.OnChangedPhoneNumber ->
                _state.value = _state.value.copy(phoneNumber = event.value)
            ConfigSimCardsEvent.OnNext ->
                _state.value = _state.value.copy(
                    currentSimCard = simCards[simCards.indexOf(_state.value.currentSimCard) + 1]
                )
            is ConfigSimCardsEvent.OnSimCardAdd -> viewModelScope.launch {
                if (isPhoneNumberValid) {
                    _state.value = _state.value.copy(isLoading = true)
                    preferences.updateSlotIndexInfoList(
                        pref.value.slotIndexInfoList.toMutableList().apply {
                            add(
                                SlotIndexInfo(
                                    _state.value.currentSimCard.slotIndex,
                                    _state.value.phoneNumber
                                )
                            )
                        }
                    )
                    repository.fetchUser(_state.value.currentSimCard.copy(phoneNumber = _state.value.phoneNumber))
                    Log.d(
                        TAG,
                        "onEvent: current slotIndex :: ${_state.value.currentSimCard.slotIndex}"
                    )
                    Log.d(
                        TAG,
                        "onEvent: last slotIndex :: ${_state.value.simCards.last().slotIndex}"
                    )
                    _state.value = _state.value.copy(isLoading = false)
                    if (_state.value.currentSimCard.slotIndex == _state.value.simCards.last().slotIndex) {
                        event.onFinish()
                    } else {
                        onEvent(ConfigSimCardsEvent.OnNext)
                    }
                }
            }
        }
    }
}
