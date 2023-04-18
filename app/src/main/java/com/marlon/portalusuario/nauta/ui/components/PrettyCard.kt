package com.marlon.portalusuario.nauta.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PrettyCard(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isFoundErrors: Boolean = false,
    content: (@Composable () -> Unit)
) {
    val shadowColor = if (isFoundErrors) MaterialTheme.colors.error else MaterialTheme.colors.onSurface

    Card(
        modifier = modifier.wrapContentSize().shadow(elevation = 8.dp, ambientColor = shadowColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Surface(shape = RoundedCornerShape(8.dp)) {
            Box {
                if (isLoading) {
                    CustomLinearProgressBar(modifier = Modifier.matchParentSize())
                }
                Surface(modifier = Modifier.padding(5.dp), shape = RoundedCornerShape(8.dp)) {
                    Box {
                        content()
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PrettyCardPreview() {
    PrettyCard(isFoundErrors = true, isLoading = true) {
        Text(text = "Hola", modifier = Modifier.padding(16.dp))
    }
}