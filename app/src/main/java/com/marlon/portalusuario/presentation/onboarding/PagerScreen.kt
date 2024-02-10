package com.marlon.portalusuario.presentation.onboarding

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.marlon.portalusuario.R

private const val WidthHalf = 0.5f

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun PagerScreen(
    onBoardingPage: OnBoardingPage,
    navigateToNext: () -> Unit,
) {
    val permissionState = onBoardingPage.permission.takeIf { it != "" }?.let {
        rememberPermissionState(permission = it)
    }

    HandleRequest(
        permissionState = permissionState,
        content = { shouldShowRationale ->
            DeniedContent(
                title = stringResource(id = onBoardingPage.title),
                deniedMessage = onBoardingPage.description,
                rationaleMessage = onBoardingPage.description,
                animation = onBoardingPage.animation,
                shouldShowRationale = shouldShowRationale,
                onRequestPermission = { permissionState?.launchPermissionRequest() }
            )
        },
        navigateToNext = navigateToNext
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun HandleRequest(
    permissionState: PermissionState?,
    content: @Composable (Boolean) -> Unit,
    navigateToNext: () -> Unit
) {
    permissionState?.let {
        when (val status = it.status) {
            is PermissionStatus.Denied -> {
                content(status.shouldShowRationale)
            }

            PermissionStatus.Granted -> {
                navigateToNext()
            }
        }
    } ?: run { content(false) }
}

@Composable
private fun DeniedContent(
    title: String,
    deniedMessage: String,
    rationaleMessage: String,
    @RawRes animation: Int,
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit = {}
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animation))

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(WidthHalf),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    modifier = Modifier.size(200.dp),
                    composition = composition
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = title,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .padding(top = 20.dp),
                text = if (shouldShowRationale) rationaleMessage else deniedMessage,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            if (title != stringResource(R.string.well_done)) {
                Button(onClick = onRequestPermission) {
                    Text(text = stringResource(R.string.request_permission))
                }
            }
        }
    }
}

@Preview
@Composable
private fun PagerScreenPreview() {
    PagerScreen(onBoardingPage = Finish) {}
}
