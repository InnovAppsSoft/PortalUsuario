package com.marlon.portalusuario.ui.components

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Downloading
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R

@Composable
fun CaptchaCanvas(
    modifier: Modifier = Modifier,
    captchaImage: Bitmap?,
    isLoading: Boolean,
    error: String?,
    onReload: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(130.dp)
            .clickable { if (!isLoading) onReload() },
        shape = MaterialTheme.shapes.small
    ) {
        if (isLoading) {
            CustomLinearProgressBar(modifier = Modifier.fillMaxSize())
        }
        Surface(
            modifier = Modifier
                .padding(if (isLoading) 5.dp else 0.dp)
                .fillMaxWidth()
        ) {
            captchaImage?.let {
                val backgroundBlur = it.copy(it.config, true)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    if (backgroundBlur != null) {
                        LegacyBlurImage(
                            modifier = Modifier.fillMaxSize(),
                            bitmap = backgroundBlur,
                            blurRadio = 25f
                        )
                    }
                } else {
                    if (backgroundBlur != null) {
                        BlurImage(
                            bitmap = backgroundBlur,
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(radiusX = 15.dp, radiusY = 15.dp)
                        )
                    }
                }
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } ?: run {
                CaptchaStatusMessage(isLoading = isLoading, error = error)
            }
        }
    }
}

@Composable
fun CaptchaStatusMessage(isLoading: Boolean, error: String?) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = if (isLoading) {
                    stringResource(R.string.loading_captcha)
                } else {
                    error ?: stringResource(R.string.captcha_load_error)
                },
                color = if (isLoading) {
                    TextFieldDefaults.colors().unfocusedTextColor
                } else {
                    MaterialTheme.colorScheme.error
                },
                textAlign = TextAlign.Center
            )
            Icon(
                imageVector = if (isLoading) Icons.Outlined.Downloading else Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                tint = if (isLoading) LocalContentColor.current else MaterialTheme.colorScheme.error
            )
        }
    }
}
