package cu.suitetecsa.cubacelmanager.presentation.balance.components.topupbottomsheet

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import cu.suitetecsa.cubacelmanager.usecases.UssdFetch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val MinLengthTopUpCode = 12
private const val MaxLengthTopUpCode = 16

@HiltViewModel
class TopUpViewModel @Inject constructor(
    private val ussdFetch: UssdFetch
) : ViewModel() {
    private val _state = mutableStateOf(TopUpSheetState())
    val state: State<TopUpSheetState>
        get() = _state

    fun onEvent(event: TopUpSheetEvent) {
        when (event) {
            is TopUpSheetEvent.OnChangeCode -> {
                _state.value = _state.value.copy(
                    topUpCode = event.code,
                    canTopUp = event.code.length in listOf(MinLengthTopUpCode, MaxLengthTopUpCode),
                    resultMessage = null
                )
            }

            is TopUpSheetEvent.OnTopUp -> {
                ussdFetch(
                    event.simCard,
                    "*662*${ _state.value.topUpCode }#",
                    { _state.value = _state.value.copy(isLoading = true) },
                    { _state.value = _state.value.copy(isLoading = false, resultMessage = it) }
                )
            }

            is TopUpSheetEvent.OnThrowError -> _state.value =
                _state.value.copy(resultMessage = event.message)
        }
    }
}
