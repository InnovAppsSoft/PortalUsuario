package com.marlon.portalusuario.nauta.ui.components.timepicker

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun TimePickerDialog(
    limitedTime: Pair<Int, Int>,
    onDismiss: () -> Unit,
    onConfirm: (Pair<Int, Int>) -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        TimerPicker(time = limitedTime, onSelectedTime = { onConfirm(it) }) {
            onDismiss()
        }
    }
}