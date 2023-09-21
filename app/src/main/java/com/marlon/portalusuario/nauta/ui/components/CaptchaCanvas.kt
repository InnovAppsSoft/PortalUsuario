package com.marlon.portalusuario.nauta.ui.components

import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.ui.BlurImage
import com.marlon.portalusuario.commons.ui.LegacyBlurImage

@Composable
fun CaptchaCanvas(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    captchaLoadStatus: Pair<Boolean, String?>,
    captchaImage: Bitmap?,
    onClick: () -> Unit
) {
    val (isOk, err) = captchaLoadStatus
    val context = LocalContext.current
    if (!isOk) Toast.makeText(context, err, Toast.LENGTH_LONG).show()
    Surface(
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp)
    ) {
        if (isLoading || !isOk) {
            if (isLoading) {
                CustomLinearProgressBar(modifier = Modifier.fillMaxSize())
            }
            Surface(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (!isOk) {
                        Column(modifier = Modifier.align(Alignment.Center)) {
                            Text(
                                text = "Error al cargar el captcha!",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.baseline_error_outline),
                                contentDescription = null,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    } else {
                        Column(modifier = Modifier.align(Alignment.Center)) {
                            Text(
                                text = "Cargando captcha...",
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.baseline_downloading_24),
                                contentDescription = null,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        } else {
            val backgroundBlur = captchaImage?.copy(captchaImage.config, true)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                if (backgroundBlur != null) {
                    LegacyBlurImage(bitmap = backgroundBlur, blurRadio = 25f)
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
            if (captchaImage != null) {
                Image(
                    bitmap = captchaImage.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}

@Preview
@Composable
fun CaptchaCanvasPreview() {
    MaterialTheme {
        CaptchaCanvas(isLoading = false, captchaLoadStatus = Pair(true, null), captchaImage = null) {

        }
    }
}