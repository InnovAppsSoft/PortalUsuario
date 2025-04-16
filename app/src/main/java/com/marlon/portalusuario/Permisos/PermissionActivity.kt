package com.marlon.portalusuario.Permisos

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.marlon.portalusuario.activities.MainActivity

private const val RESULT_CALL: Int = 1001

class PermissionActivity : AppCompatActivity() {
    private val viewModel: PermissionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PermissionScreen(viewModel, this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RESULT_CALL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.nextStep()
            }
        }
    }
}

@Composable
fun PermissionScreen(viewModel: PermissionViewModel, activity: Activity) {
    val context = LocalContext.current
    val overlayPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        val canDrawOverlays = Settings.canDrawOverlays(context)
        viewModel.onOverlayPermissionResult(canDrawOverlays)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState())
            .background(color = Color(0xFFF5F5F5)) // Color de fondo claro
    ) {
        Text(
            text = "Permisos",
            fontSize = 24.sp,
            color = Color(0xFF3A3A3A),
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                PermissionSteps(viewModel, overlayPermissionLauncher, activity)
            }
        }
    }
}

@Composable
fun PermissionSteps(
    viewModel: PermissionViewModel,
    overlayPermissionLauncher: ActivityResultLauncher<Intent>,
    activity: Activity
) {
    val context = LocalContext.current

    val locationArray =
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS
        )

    val steps = listOf(
        "Permiso para realizar llamadas",
        "Permiso para usar la cámara",
        "Permiso para leer contactos",
        "Permiso para obtener ubicación",
        "Permiso de superposición",
        "¡Permisos concedidos!"
    )

    Column {
        steps.forEachIndexed { index, step ->
            val isButtonEnabled = index == viewModel.currentStep

            Text(
                text = step,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                color = Color(0xFF3A3A3A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Button(
                onClick = {
                    when (index) {
                        0 -> ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.CALL_PHONE),
                            RESULT_CALL
                        )
                        1 -> ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.CAMERA),
                            RESULT_CALL
                        )
                        2 -> ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.READ_CONTACTS),
                            RESULT_CALL
                        )
                        3 -> ActivityCompat.requestPermissions(
                            activity,
                            locationArray,
                            RESULT_CALL
                        )
                        4 -> {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                "package:${context.packageName}".toUri()
                            )
                            overlayPermissionLauncher.launch(intent)
                        }
                        else -> {
                            context.startActivity(Intent(context, MainActivity::class.java))
                            (context as Activity).finish()
                        }
                    }
                },
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(text = if (index == steps.size - 1) "Comenzar" else "Conceder")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionScreenPreview() {
    val viewModel = PermissionViewModel()
    PermissionScreen(viewModel, Activity())
}
