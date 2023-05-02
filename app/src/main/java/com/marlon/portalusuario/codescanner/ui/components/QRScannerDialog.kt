package com.marlon.portalusuario.codescanner.ui.components

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.marlon.portalusuario.nauta.ui.components.CameraPermissionRequestDialog
import com.marlon.portalusuario.nauta.ui.components.PrettyCard

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRScannerDialog(show: Boolean, onQRCodeRead: (String) -> Unit, onDismiss: () -> Unit) {
    if (show) {
        val cameraPermissionState = rememberPermissionState(
            Manifest.permission.CAMERA
        )
        var showOtherDialog by rememberSaveable { mutableStateOf(false) }

        if (cameraPermissionState.status.isGranted) {
            Dialog(
                onDismissRequest = { onDismiss() },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                PrettyCard { CameraPreview(onQRCodeRead) }
            }
        } else if (cameraPermissionState.status.shouldShowRationale) {
            CameraPermissionRequestDialog(show = showOtherDialog) { showOtherDialog = false }
        } else {
            CameraPermissionRequestDialog(show = showOtherDialog) { showOtherDialog = false }
        }
    }

}