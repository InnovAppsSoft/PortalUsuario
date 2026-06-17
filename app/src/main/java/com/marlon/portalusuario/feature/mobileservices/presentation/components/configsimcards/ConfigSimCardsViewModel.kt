package com.marlon.portalusuario.feature.mobileservices.presentation.components.configsimcards

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.data.preferences.IMobServicesPreferences
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.domain.model.MobServPreferences
import com.marlon.portalusuario.domain.model.SlotIndexInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.suitetecsa.sdk.android.SimCardCollector
import io.github.suitetecsa.sdk.android.model.SimCard
import io.github.suitetecsa.sdk.android.utils.extractShortNumber
import io.github.suitetecsa.sdk.android.utils.validateFormat
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ConfigSimCardsViewModel"

@HiltViewModel
class ConfigSimCardsViewModel
    @Inject
    constructor(
        private val preferences: IMobServicesPreferences,
        private val repository: UserRepository,
        private val simCardCollector: SimCardCollector,
    ) : ViewModel() {
        private val pref =
            preferences.preferences.stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                MobServPreferences(emptyList()),
            )

        @SuppressLint("MissingPermission")
        private suspend fun loadSimCards(): List<SimCard> {
            val indexes = preferences.preferences.first().slotIndexInfoList
            return simCardCollector.collect().filter {
                it.slotIndex !in indexes.map { index -> index.index }
            }
        }

        private val _state =
            mutableStateOf(
                ConfigSimCardsState(),
            )
        val state get() = _state.value

        val isPhoneNumberValid: Boolean
            get() = validateFormat(_state.value.phoneNumber)?.length == 8

        init {
            viewModelScope.launch {
                val cards = loadSimCards()
                _state.value =
                    ConfigSimCardsState(
                        simCards = cards,
                        currentSimCard = cards.first(),
                        phoneNumber =
                            cards.first().phoneNumber?.let { extractShortNumber(it) }
                                ?: "",
                    )
            }
        }

        fun onEvent(event: ConfigSimCardsEvent) {
            when (event) {
                is ConfigSimCardsEvent.OnChangedPhoneNumber ->
                    _state.value = _state.value.copy(phoneNumber = event.value)
                ConfigSimCardsEvent.OnNext -> {
                    val cards = _state.value.simCards
                    val current = _state.value.currentSimCard!!
                    val idx = cards.indexOf(current)
                    if (idx >= 0 && idx < cards.size - 1) {
                        _state.value =
                            _state.value.copy(
                                currentSimCard = cards[idx + 1],
                            )
                    }
                }
                is ConfigSimCardsEvent.OnSimCardAdd ->
                    viewModelScope.launch {
                        if (isPhoneNumberValid) {
                            _state.value = _state.value.copy(isLoading = true)
                            preferences.updateSlotIndexInfoList(
                                pref.value.slotIndexInfoList.toMutableList().apply {
                                    add(
                                        SlotIndexInfo(
                                            _state.value.currentSimCard!!!!.slotIndex,
                                            _state.value.phoneNumber,
                                        ),
                                    )
                                },
                            )
                            repository.fetchUser(
                                _state.value.currentSimCard!!.copy(phoneNumber = _state.value.phoneNumber),
                            )
                            Log.d(
                                TAG,
                                "onEvent: current slotIndex :: ${_state.value.currentSimCard!!.slotIndex}",
                            )
                            Log.d(
                                TAG,
                                "onEvent: last slotIndex :: ${_state.value.simCards.last().slotIndex}",
                            )
                            _state.value = _state.value.copy(isLoading = false)
                            if (_state.value.currentSimCard!!.slotIndex ==
                                _state.value.simCards
                                    .last()
                                    .slotIndex
                            ) {
                                event.onFinish()
                            } else {
                                onEvent(ConfigSimCardsEvent.OnNext)
                            }
                        }
                    }
            }
        }
    }
