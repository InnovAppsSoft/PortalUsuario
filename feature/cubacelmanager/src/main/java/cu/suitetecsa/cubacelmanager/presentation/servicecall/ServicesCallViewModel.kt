package cu.suitetecsa.cubacelmanager.presentation.servicecall

import android.annotation.SuppressLint
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.suitetecsa.cubacelmanager.data.source.PreferencesDataSource
import cu.suitetecsa.cubacelmanager.domain.model.Preferences
import cu.suitetecsa.cubacelmanager.usecases.ExecuteUSSD
import cu.suitetecsa.sdk.android.SimCardCollector
import cu.suitetecsa.sdk.android.model.SimCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ServicesCallViewModel @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    private val simCardCollector: SimCardCollector,
    private val executeUSSD: ExecuteUSSD,
) : ViewModel() {
    private val preferences: StateFlow<Preferences> = preferencesDataSource.preferences()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = Preferences("")
        )
    private val currentSimCardId: String
        get() = preferences.value.currentSimCardId
    private val simCards: List<SimCard>
        @SuppressLint("MissingPermission")
        get() = simCardCollector.collect()
    private val currentSimCard: SimCard?
        get() {
            return if (currentSimCardId.isNotBlank()) {
                simCards.firstOrNull {
                    it.serialNumber == preferences.value.currentSimCardId
                }.let { it ?: simCards.firstOrNull() }
            } else {
                simCards.firstOrNull()
            }
        }

    private val _state = mutableStateOf(ServiceCallState(simCards = simCards, currentSimCard = currentSimCard))
    val state: State<ServiceCallState>
        get() = _state

    @SuppressLint("MissingPermission")
    fun onEvent(event: ServiceCallEvent) {
        when (event) {
            is ServiceCallEvent.Call -> {
                currentSimCard?.let { executeUSSD(it, event.number) }
            }
            is ServiceCallEvent.ChangeSimCard -> {
                viewModelScope.launch {
                    preferencesDataSource.updateCurrentSimCardId(
                        event.simCard.serialNumber ?: event.simCard.subscriptionId.toString()
                    )
                    _state.value = _state.value.copy(currentSimCard = currentSimCard, simCards = simCards)
                }
            }
        }
    }
}
