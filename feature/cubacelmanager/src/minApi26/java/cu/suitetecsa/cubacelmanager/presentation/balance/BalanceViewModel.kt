package cu.suitetecsa.cubacelmanager.presentation.balance

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.suitetecsa.core.ui.components.rechargeview.RechargeViewState
import cu.suitetecsa.cubacelmanager.data.source.PreferencesDataSource
import cu.suitetecsa.cubacelmanager.data.sources.BalanceRepository
import cu.suitetecsa.cubacelmanager.domain.model.Balance
import cu.suitetecsa.cubacelmanager.domain.model.Preferences
import cu.suitetecsa.cubacelmanager.presentation.balance.components.cardtransfer.CardTransferState
import cu.suitetecsa.cubacelmanager.usecases.CustomConsult
import cu.suitetecsa.cubacelmanager.usecases.ExecuteUSSD
import cu.suitetecsa.cubacelmanager.usecases.GetConsultMessage
import cu.suitetecsa.cubacelmanager.usecases.UpdateBalance
import cu.suitetecsa.sdk.android.SimCardCollector
import cu.suitetecsa.sdk.android.balance.FetchBalanceCallBack
import cu.suitetecsa.sdk.android.balance.consult.UssdRequest
import cu.suitetecsa.sdk.android.balance.response.Custom
import cu.suitetecsa.sdk.android.balance.response.UssdResponse
import cu.suitetecsa.sdk.android.kotlin.ussdFetch
import cu.suitetecsa.sdk.android.model.SimCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class BalanceViewModel @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    private val balanceRepository: BalanceRepository,
    private val simCardCollector: SimCardCollector,
    private val updateBalance: UpdateBalance,
    private val getConsultMessage: GetConsultMessage,
    private val executeUSSD: ExecuteUSSD
) : ViewModel() {
    private val balances: StateFlow<List<Balance>> = balanceRepository.getBalances().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = listOf()
    )
    private val preferences: StateFlow<Preferences> = preferencesDataSource.preferences().stateIn(
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

    private val _balance = mutableStateOf(
        balances.value.firstOrNull { it.subscriptionID == currentSimCard?.subscriptionId }
    )
    val balance: State<Balance?>
        get() = _balance

    private val _state = mutableStateOf(
        BalanceState(
            canRun = simCards.isNotEmpty(),
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

    init {
        viewModelScope.launch {
            balanceRepository.getBalances().collect { balanceList ->
                _balance.value = balanceList.firstOrNull { it.subscriptionID == currentSimCard?.subscriptionId }
            }
            preferencesDataSource.preferences().collect { preferencesCollected ->
                updateState(
                    _state.value.copy(
                        currentSimCard = simCards.firstOrNull {
                            it.serialNumber == preferencesCollected.currentSimCardId
                        }
                    )
                )
            }
        }
    }

    @SuppressLint("MissingPermission", "NewApi")
    fun onEvent(event: BalanceEvent) {
        when (event) {
            is BalanceEvent.ChangeAmountToTransfer -> updateState(
                _state.value.copy(
                    transferState = _state.value.transferState.copy(amount = event.amount)
                )
            )

            is BalanceEvent.ChangeNumberToTransfer -> updateState(
                _state.value.copy(
                    transferState = _state.value.transferState.copy(dest = event.number)
                )
            )

            is BalanceEvent.ChangePinPassword -> updateState(
                _state.value.copy(
                    transferState = _state.value.transferState.copy(pinPassword = event.pinPassword)
                )
            )

            is BalanceEvent.ChangeRechargeCode -> {
                _state.value = _state.value.copy(
                    rechargeState = _state.value.rechargeState.copy(code = event.code)
                )
            }

            is BalanceEvent.ChangeSimCard -> {
                viewModelScope.launch {
                    preferencesDataSource.updateCurrentSimCardId(event.simCard.serialNumber)
                    updateState(_state.value.copy(currentSimCard = currentSimCard, simCards = simCards))
                }
            }

            BalanceEvent.TopUpBalance -> topUpBalance()

            BalanceEvent.TransferFunds -> transferFunds()

            is BalanceEvent.TurnUsageBasedPricing -> turnUsageBasedPricing(event.active)
            BalanceEvent.UpdateBalance -> {
                updateState(_state.value.copy(canRun = false, loading = true))
                _state.value.currentSimCard?.let {
                    updateBalance(it, ::updateRunningMessage) {
                        updateState(_state.value.copy(canRun = true, loading = false))
                    }
                }
            }

            BalanceEvent.DismissDialog -> {
                updateState(_state.value.copy(resultMessage = null))
            }
        }
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    private fun transferFunds() {
        _state.value.currentSimCard?.let {
            executeUSSD(
                it,
                "*234*1*${_state.value.transferState.dest}*" +
                    "${_state.value.transferState.pinPassword}*" +
                    "${_state.value.transferState.amount}${Uri.parse("#")}"
            )
        }
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    private fun topUpBalance() {
        _state.value.currentSimCard?.ussdFetch(
            "*662*${_state.value.rechargeState.code}${Uri.parse("#")}",
            object : FetchBalanceCallBack {
                override fun onFailure(throwable: Throwable) {
                    updateState(
                        _state.value.copy(
                            runningMessage = null,
                            loading = false,
                            canRun = true,
                            rechargeState = _state.value.rechargeState.copy(isLoading = false),
                            resultMessage = throwable.message.toString()
                        )
                    )
                }

                override fun onFetching(request: UssdRequest) {
                    updateState(
                        _state.value.copy(
                            canRun = false,
                            loading = true,
                            runningMessage = getConsultMessage(
                                request,
                                customType = CustomConsult.TopUpBalance
                            ),
                            rechargeState = _state.value.rechargeState.copy(isLoading = true, code = ""),
                        )
                    )
                }

                override fun onSuccess(request: UssdRequest, response: UssdResponse) {
                    updateState(
                        _state.value.copy(
                            runningMessage = null,
                            loading = false,
                            canRun = true,
                            rechargeState = _state.value.rechargeState.copy(isLoading = false),
                            resultMessage = (response as Custom).response,
                        )
                    )
                }
            }
        )
    }

    private fun updateState(state: BalanceState) {
        _state.value = state
    }

    private fun updateRunningMessage(message: String?) =
        updateState(_state.value.copy(runningMessage = message))

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    private fun turnUsageBasedPricing(active: Boolean) {
        val ussdCode = if (active) "*133*1*1*1${Uri.parse("#")}" else "*133*1*1*2${Uri.parse("#")}"
        _state.value.currentSimCard?.ussdFetch(
            ussdCode,
            object : FetchBalanceCallBack {
                override fun onFailure(throwable: Throwable) {
                    _state.value = _state.value.copy(
                        runningMessage = null,
                        loading = false,
                        canRun = true,
                    )
                    throwable.printStackTrace()
                }

                override fun onFetching(request: UssdRequest) {
                    _state.value = _state.value.copy(
                        loading = true,
                        canRun = false,
                        runningMessage = getConsultMessage(
                            request,
                            customType = CustomConsult.TurnUsageBasedPricing(active)
                        ),
                    )
                }

                override fun onSuccess(request: UssdRequest, response: UssdResponse) {
                    when (request) {
                        UssdRequest.CUSTOM -> {
                            if ((response as Custom).response == UssdResponse.PROCESSING_RESPONSE) {
                                viewModelScope.launch {
                                    _state.value = _state.value.copy(
                                        runningMessage = null,
                                        loading = false,
                                        canRun = true
                                    )
                                    balanceRepository.updateBalance(
                                        _balance.value!!.copy(
                                            usageBasedPricing = active
                                        )
                                    )
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
        )
    }
}
