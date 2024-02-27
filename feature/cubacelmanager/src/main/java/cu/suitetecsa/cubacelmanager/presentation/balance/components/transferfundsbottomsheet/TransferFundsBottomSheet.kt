package cu.suitetecsa.cubacelmanager.presentation.balance.components.transferfundsbottomsheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import cu.suitetecsa.cubacelmanager.presentation.balance.components.transferfundsbottomsheet.TransferFundsSheetEvent.OnCollectContacts
import cu.suitetecsa.sdk.android.model.Contact
import cu.suitetecsa.sdk.android.model.SimCard
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun TransferFundsBottomSheet(
    state: TransferFundsSheetState,
    isVisible: Boolean = false,
    currentSimCard: SimCard,
    onEvent: (TransferFundsSheetEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val pagerState = rememberPagerState { 2 }

    val pages = listOf(
        TransferFundsPage.Transfer(
            state = state,
            onEvent = onEvent,
            currentSimCard = currentSimCard,
            onContactSelect = {
                onEvent(OnCollectContacts)
                scope.launch {
                    pagerState.animateScrollToPage(1)
                }
                keyboardController?.hide()
            }
        ),
        TransferFundsPage.Contacts(contacts = state.contacts) {
            onEvent(TransferFundsSheetEvent.OnChangeDest(it.phoneNumber))
            scope.launch { pagerState.animateScrollToPage(0) }
        }
    )

    if (isVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            windowInsets = WindowInsets.ime,
            dragHandle = {
                DragContent(state = state) {
                    onEvent(TransferFundsSheetEvent.OnTransferFunds(currentSimCard))
                }
            },
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                    onEvent(TransferFundsSheetEvent.OnChangeDest(""))
                    onEvent(TransferFundsSheetEvent.OnChangeAmount(""))
                    onEvent(TransferFundsSheetEvent.OnChangePin(""))
                }
            }
        ) {
            HorizontalPager(state = pagerState, userScrollEnabled = false) { index ->
                SheetContent(page = pages[index])
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DragContent(
    state: TransferFundsSheetState,
    onTransfer: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BottomSheetDefaults.DragHandle()
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Transfer funds",
                style = MaterialTheme.typography.titleLarge
            )
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                IconButton(
                    enabled = !state.isLoading,
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Outlined.QrCodeScanner,
                        contentDescription = null
                    )
                }
                TextButton(
                    enabled = !state.isLoading && state.canTransfer,
                    onClick = onTransfer
                ) {
                    Text(text = "Transfer")
                }
            }
        }
    }
}

@Composable
fun SheetContent(page: TransferFundsPage) {
    when (page) {
        is TransferFundsPage.Contacts -> {
            ContactsPage(contacts = page.contacts, onContactClick = page.onContactClick)
        }
        is TransferFundsPage.Transfer -> {
            TransferFundsPage(
                state = page.state,
                currentSimCard = page.currentSimCard,
                onEvent = page.onEvent,
                onContactSelect = page.onContactSelect
            )
        }
    }
}

sealed class TransferFundsPage {
    data class Transfer(
        val state: TransferFundsSheetState,
        val currentSimCard: SimCard,
        val onEvent: (TransferFundsSheetEvent) -> Unit,
        val onContactSelect: () -> Unit
    ) : TransferFundsPage()

    data class Contacts(
        val contacts: List<Contact>,
        val onContactClick: (Contact) -> Unit
    ) : TransferFundsPage()
}
