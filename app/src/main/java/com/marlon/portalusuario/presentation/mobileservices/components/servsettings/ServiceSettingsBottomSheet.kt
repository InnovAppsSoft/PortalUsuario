package com.marlon.portalusuario.presentation.mobileservices.components.servsettings

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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.marlon.portalusuario.domain.model.MobileService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceSettingsBottomSheet(
    mobService: MobileService,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var isLoading by remember { mutableStateOf(false) }

    ModalBottomSheet(
        sheetState = sheetState,
        contentWindowInsets = { WindowInsets.ime },
        dragHandle = { DragContent(isLoading = isLoading) },
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        }
    ) { ServiceSettingsView(mobService = mobService, onSetIsLoading = { isLoading = it }) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DragContent(isLoading: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BottomSheetDefaults.DragHandle()
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(),
                text = "Configuracion",
                style = MaterialTheme.typography.titleLarge
            )
        }
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}
