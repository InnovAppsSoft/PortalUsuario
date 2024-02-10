package cu.suitetecsa.cubacelmanager.presentation.balance

import android.annotation.SuppressLint
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.suitetecsa.core.ui.components.rechargeview.RechargeViewState
import cu.suitetecsa.cubacelmanager.data.source.PreferencesDataSource
import cu.suitetecsa.cubacelmanager.domain.model.Preferences
import cu.suitetecsa.cubacelmanager.presentation.balance.components.cardtransfer.CardTransferState
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
class BalanceViewModel @Inject constructor(
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

    private val _state = mutableStateOf(
        BalanceState(
            simCards = simCards,
            currentSimCard = currentSimCard,
            rechargeState = RechargeViewState(
                onTopUp = { onEvent(BalanceEvent.TopUpBalance) },
                onChangeTopUpCode = { onEvent(BalanceEvent.ChangeRechargeCode(it)) },
            ),
            transferState = CardTransferState(
                onChangeDest = { onEvent(BalanceEvent.ChangeNumberToTransfer(it)) },
                onChangeAmount = { onEvent(BalanceEvent.ChangeAmountToTransfer(it)) },
                onChangePinPassword = { onEvent(BalanceEvent.ChangePinPassword(it)) }
            )
        )
    )
    val state: State<BalanceState>
        get() = _state

    @SuppressLint("MissingPermission", "NewApi")
    fun onEvent(event: BalanceEvent) {
        when (event) {
            is BalanceEvent.ChangeAmountToTransfer -> {
                _state.value = _state.value.copy(
                    transferState = _state.value.transferState.copy(
                        amount = event.amount
                    )
                )
            }

            is BalanceEvent.ChangeNumberToTransfer -> {
                _state.value = _state.value.copy(
                    transferState = _state.value.transferState.copy(
                        dest = event.number
                    )
                )
            }

            is BalanceEvent.ChangePinPassword -> {
                _state.value = _state.value.copy(
                    transferState = _state.value.transferState.copy(
                        pinPassword = event.pinPassword
                    )
                )
            }

            is BalanceEvent.ChangeRechargeCode -> {
                _state.value = _state.value.copy(
                    rechargeState = _state.value.rechargeState.copy(
                        code = event.code
                    )
                )
            }

            is BalanceEvent.ChangeSimCard -> {
                viewModelScope.launch {
                    preferencesDataSource.updateCurrentSimCardId(event.simCard.serialNumber)
                    _state.value = _state.value.copy(currentSimCard = currentSimCard, simCards = simCards)
                }
            }

            is BalanceEvent.MakeCall -> {
                currentSimCard?.let { executeUSSD(it, event.ussdCode, {}) {} }
            }
            BalanceEvent.TopUpBalance -> {
                currentSimCard?.let {
                    executeUSSD(it, "*662*${_state.value.rechargeState.code}%23", {}) {}
                }
            }

            BalanceEvent.TransferFunds -> {
                currentSimCard?.let {
                    executeUSSD(it, "", {}) {}
                }
            }
        }
    }
}
