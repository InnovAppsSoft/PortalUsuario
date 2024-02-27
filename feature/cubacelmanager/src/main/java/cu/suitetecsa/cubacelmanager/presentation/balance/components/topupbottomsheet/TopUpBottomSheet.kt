package cu.suitetecsa.cubacelmanager.presentation.balance.components.topupbottomsheet

import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.journeyapps.barcodescanner.ScanContract
import cu.suitetecsa.core.ui.R
import cu.suitetecsa.core.ui.components.animateplaceholder.AnimatedPlaceholder
import cu.suitetecsa.core.ui.components.rechargeview.rechargeCodeVisualTransformation
import cu.suitetecsa.core.ui.theme.SuitEtecsaTheme
import cu.suitetecsa.core.utils.SuitEtecsaUtils.isValidTopUpCode
import cu.suitetecsa.core.utils.SuitEtecsaUtils.qrOptions
import cu.suitetecsa.cubacelmanager.presentation.balance.components.topupbottomsheet.TopUpSheetEvent.OnChangeCode
import cu.suitetecsa.cubacelmanager.presentation.balance.components.topupbottomsheet.TopUpSheetEvent.OnTopUp
import cu.suitetecsa.sdk.android.model.SimCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopUpBottomSheet(
    state: TopUpSheetState,
    isVisible: Boolean,
    currentSimCard: SimCard,
    onEvent: (TopUpSheetEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    if (isVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            windowInsets = WindowInsets.ime,
            dragHandle = { DragContent(state.isLoading, state.canTopUp) { onEvent(OnTopUp(currentSimCard)) } },
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                    onEvent(OnChangeCode(""))
                }
            }
        ) {
            SheetContent(
                state = state,
                currentSimCard = currentSimCard,
                onEvent = onEvent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DragContent(
    isLoading: Boolean,
    canRun: Boolean,
    onTopUp: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BottomSheetDefaults.DragHandle()
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(id = R.string.recharge_code_placeholder),
                style = MaterialTheme.typography.titleLarge
            )
            TextButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                enabled = !isLoading && canRun,
                onClick = onTopUp
            ) {
                Text(text = "Top up")
            }
        }
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SheetContent(
    state: TopUpSheetState,
    currentSimCard: SimCard,
    onEvent: (TopUpSheetEvent) -> Unit,
) {
    val errorMessage = stringResource(cu.suitetecsa.cubacelmanager.R.string.invalid_top_up_code_message)
    val prompt = stringResource(id = cu.suitetecsa.cubacelmanager.R.string.scan_recharge_code)
    val scanLauncher = rememberLauncherForActivityResult(contract = ScanContract()) { result ->
        if (result.contents.isValidTopUpCode) {
            onEvent(OnChangeCode(result.contents))
        } else { onEvent(TopUpSheetEvent.OnThrowError(errorMessage)) }
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column {
        state.resultMessage?.let {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp).clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = .1f))
                    .fillMaxWidth().wrapContentSize().padding(4.dp),
                text = it
            )
        }
        TextField(
            value = state.topUpCode,
            onValueChange = { code -> onEvent(OnChangeCode(code.takeWhile { it.isDigit() })) },
            visualTransformation = rechargeCodeVisualTransformation(), enabled = !state.isLoading,
            placeholder = {
                AnimatedPlaceholder(
                    hints = listOf(
                        stringResource(R.string.recharge_code_placeholder),
                        stringResource(R.string.recharge_code_example)
                    )
                )
            },
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(onGo = {
                if (!state.isLoading && state.canTopUp) {
                    keyboardController?.hide()
                    onEvent(OnTopUp(currentSimCard))
                }
            }),
            shape = MaterialTheme.shapes.small, singleLine = true, maxLines = 1,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            leadingIcon = { Icon(imageVector = Icons.Outlined.Numbers, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { scanLauncher.launch(qrOptions.apply { setPrompt(prompt) }) }) {
                    Icon(imageVector = Icons.Outlined.QrCodeScanner, contentDescription = null)
                }
            }
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)
@Composable
private fun SheetContentPreview() {
    SuitEtecsaTheme {
        Surface {
            SheetContent(
                state = TopUpSheetState(resultMessage = "Su cuenta bla bla bla"),
                currentSimCard = SimCard("", "", null, 0, 0, null)
            ) {}
        }
    }
}
