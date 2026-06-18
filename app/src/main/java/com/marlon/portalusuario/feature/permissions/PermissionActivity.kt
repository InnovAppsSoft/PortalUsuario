package com.marlon.portalusuario.feature.permissions

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri

@Composable
fun PermissionScreen(
    viewModel: PermissionViewModel,
    onFinish: () -> Unit,
) {
    val context = LocalContext.current

    val callPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) viewModel.nextStep()
        }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) viewModel.nextStep()
        }

    val contactsPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) viewModel.nextStep()
        }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.values.all { it }) viewModel.nextStep()
        }

    val overlayPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            val canDrawOverlays = Settings.canDrawOverlays(context)
            viewModel.onOverlayPermissionResult(canDrawOverlays)
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
                .background(color = Color(0xFFF5F5F5)),
    ) {
        Text(
            text = "Permisos",
            fontSize = 24.sp,
            color = Color(0xFF3A3A3A),
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            modifier =
                Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                PermissionSteps(
                    viewModel = viewModel,
                    onRequestCallPermission = { callPermissionLauncher.launch(Manifest.permission.CALL_PHONE) },
                    onRequestCameraPermission = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                    onRequestContactsPermission = { contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS) },
                    onRequestLocationPermission = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.READ_PHONE_NUMBERS,
                            ),
                        )
                    },
                    onRequestOverlayPermission = {
                        val intent =
                            Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                "package:${context.packageName}".toUri(),
                            )
                        overlayPermissionLauncher.launch(intent)
                    },
                    onFinish = onFinish,
                )
            }
        }
    }
}

@Composable
fun PermissionSteps(
    viewModel: PermissionViewModel,
    onRequestCallPermission: () -> Unit,
    onRequestCameraPermission: () -> Unit,
    onRequestContactsPermission: () -> Unit,
    onRequestLocationPermission: () -> Unit,
    onRequestOverlayPermission: () -> Unit,
    onFinish: () -> Unit,
) {
    val steps =
        listOf(
            "Permiso para realizar llamadas",
            "Permiso para usar la cámara",
            "Permiso para leer contactos",
            "Permiso para obtener ubicación",
            "Permiso de superposición",
            "¡Permisos concedidos!",
        )

    Column {
        steps.forEachIndexed { index, step ->
            val isButtonEnabled = index == viewModel.currentStep

            Text(
                text = step,
                modifier =
                    Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                color = Color(0xFF3A3A3A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
            )
            Button(
                onClick = {
                    when (index) {
                        0 -> onRequestCallPermission()
                        1 -> onRequestCameraPermission()
                        2 -> onRequestContactsPermission()
                        3 -> onRequestLocationPermission()
                        4 -> onRequestOverlayPermission()
                        else -> onFinish()
                    }
                },
                enabled = isButtonEnabled,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
            ) {
                Text(text = if (index == steps.size - 1) "Comenzar" else "Conceder")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionScreenPreview() {
    PermissionScreen(PermissionViewModel()) {}
}
