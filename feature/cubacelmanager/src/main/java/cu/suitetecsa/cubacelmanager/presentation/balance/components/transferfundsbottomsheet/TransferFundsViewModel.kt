package cu.suitetecsa.cubacelmanager.presentation.balance.components.transferfundsbottomsheet

import android.annotation.SuppressLint
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import cu.suitetecsa.cubacelmanager.usecases.ExecuteUSSD
import cu.suitetecsa.sdk.android.ContactsCollector
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransferFundsViewModel @Inject constructor(
    private val contactsCollector: ContactsCollector,
    private val executeUSSD: ExecuteUSSD
) : ViewModel() {
    private val _state = mutableStateOf(
        TransferFundsSheetState(contacts = contactsCollector.collect())
    )
    val state: State<TransferFundsSheetState>
        get() = _state

    private val canTransfer: Boolean
        get() {
            return _state.value.dest.length == 8 &&
                _state.value.dest.startsWith("5") &&
                _state.value.amount.isNotBlank() &&
                _state.value.pin.length >= 4
        }

    @SuppressLint("MissingPermission")
    fun onEvent(event: TransferFundsSheetEvent) {
        when (event) {
            is TransferFundsSheetEvent.OnChangeAmount -> {
                _state.value = _state.value.copy(amount = event.amount)
                _state.value = _state.value.copy(canTransfer = canTransfer)
            }
            is TransferFundsSheetEvent.OnChangeDest -> {
                _state.value = _state.value.copy(dest = event.dest)
                _state.value = _state.value.copy(canTransfer = canTransfer)
            }
            is TransferFundsSheetEvent.OnChangePin -> {
                _state.value = _state.value.copy(pin = event.pin)
                _state.value = _state.value.copy(canTransfer = canTransfer)
            }
            is TransferFundsSheetEvent.OnTransferFunds -> {
                executeUSSD(
                    event.simCard,
                    "*234*1*${_state.value.dest}*${_state.value.pin}*${_state.value.amount}#"
                )
            }
            TransferFundsSheetEvent.OnCollectContacts -> {
                _state.value = _state.value.copy(contacts = contactsCollector.collect())
            }
        }
    }
}
