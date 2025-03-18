package com.marlon.portalusuario.presentation.mobileservices.components.configsimcards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigSimCardsBottomSheet(onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val configViewState = rememberConfigSimCardsViewState()
    var isLoading by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var canConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(
        sheetState = sheetState,
        contentWindowInsets = { WindowInsets.ime },
        dragHandle = {
            DragContent(
                title = title,
                onConfirm = {
                    configViewState.onNext {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    }
                },
                isLoading = isLoading,
                canConfirm = canConfirm
            )
        },
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        }
    ) {
        ConfigSimCardsView(configViewState, { isLoading = it }, { title = it }, { canConfirm = it })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DragContent(title: String, onConfirm: () -> Unit, isLoading: Boolean, canConfirm: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BottomSheetDefaults.DragHandle()
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(),
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            TextButton(
                onClick = onConfirm,
                enabled = !isLoading && canConfirm,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(text = "Confirmar")
            }
        }
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}
