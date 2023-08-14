package com.marlon.portalusuario.nauta.ui.components.captchaview

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.ui.BlurImage
import com.marlon.portalusuario.commons.ui.LegacyBlurImage
import com.marlon.portalusuario.nauta.ui.components.CustomLinearProgressBar
import com.marlon.portalusuario.nauta.ui.components.captchaview.CaptchaViewState.Failure
import com.marlon.portalusuario.nauta.ui.components.captchaview.CaptchaViewState.Loading
import com.marlon.portalusuario.nauta.ui.components.captchaview.CaptchaViewState.Success

@Composable
fun CaptchaView(
    modifier: Modifier = Modifier,
    state: CaptchaViewState,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp)
    ) {
        when (state) {
            is Failure, Loading -> {
                if (state == Loading) CustomLinearProgressBar(modifier = Modifier.fillMaxSize())

                Surface(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .height(130.dp)
                        .clickable { onClick() }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (state is Failure) {
                            Column(modifier = Modifier.align(Alignment.Center)) {
                                Text(
                                    text = state.reason,
                                    color = MaterialTheme.colors.error,
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
            }

            is Success -> {
                val backgroundBlur = state.image.copy(state.image.config, true)
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
                Image(
                    bitmap = state.image.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun CaptchaCanvasPreview() {
    MaterialTheme {
        CaptchaView(state = Failure("timeout")) { }
    }
}