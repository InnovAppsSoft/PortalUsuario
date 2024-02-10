package com.marlon.portalusuario.presentation.splash

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.marlon.portalusuario.R
import kotlinx.coroutines.delay

private const val DelayDuration = 2000L

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SplashScreen(navigateToMain: () -> Unit, navigateToOnBoarding: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
        )
        val missingPermissions = mutableListOf<String>()
        val multiplePermissionsState = rememberMultiplePermissionsState(permissions = permissions)
        val result = multiplePermissionsState.permissions.all {
            it.status == PermissionStatus.Granted
        }
        if (result) {
            LaunchedEffect(key1 = true) {
                delay(DelayDuration)
                navigateToMain()
            }
        } else {
            multiplePermissionsState.permissions.forEach {
                if (it.status != PermissionStatus.Granted) {
                    missingPermissions.add(it.permission)
                }
            }
            LaunchedEffect(key1 = true) {
                delay(DelayDuration)
                navigateToOnBoarding()
            }
        }
    } else {
        LaunchedEffect(key1 = true) {
            delay(DelayDuration)
            navigateToMain()
        }
    }
    Splash()
}

@Composable
fun Splash() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(150.dp, 150.dp),
            painter = painterResource(id = R.mipmap.ic_launcher),
            contentDescription = null
        )
        Text(text = "Bienvenidos", fontSize = 30.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenView() {
    SplashScreen(navigateToMain = {}, navigateToOnBoarding = {})
}
